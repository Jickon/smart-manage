package sm.system.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import sm.system.helper.SM4Helper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * 接口加解密过滤器，通过预扫路径集合匹配是否需要加解密
 *
 * @author Chekfu
 */
@Slf4j
public class EncryptApiFilter implements Filter {

    private final SM4Helper sm4Helper;
    private final Set<String> encryptPaths;
    private final Set<String> decryptPaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public EncryptApiFilter(SM4Helper sm4Helper, Set<String> encryptPaths, Set<String> decryptPaths) {
        this.sm4Helper = sm4Helper;
        this.encryptPaths = encryptPaths;
        this.decryptPaths = decryptPaths;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestUri = request.getRequestURI();
        boolean needEncrypt = matches(requestUri, encryptPaths);
        boolean needDecrypt = matches(requestUri, decryptPaths);

        // 请求解密
        ServletRequest requestToUse = request;
        if (needDecrypt) {
            String cipherBody = readBody(request);
            if (cipherBody != null && !cipherBody.isBlank()) {
                String plainJson = sm4Helper.decrypt(cipherBody.trim());
                requestToUse = new DecryptRequestWrapper(request, plainJson);
            }
        }

        // 响应加密
        ServletResponse responseToUse = response;
        ByteArrayResponseWrapper responseWrapper = null;
        if (needEncrypt) {
            responseWrapper = new ByteArrayResponseWrapper(response);
            responseToUse = responseWrapper;
        }

        chain.doFilter(requestToUse, responseToUse);

        if (responseWrapper != null) {
            byte[] capturedBytes = responseWrapper.getCapturedBytes();
            String plainJson = new String(capturedBytes, StandardCharsets.UTF_8);
            String ciphertext = sm4Helper.encrypt(plainJson);
            response.setContentType("text/plain;charset=UTF-8");
            response.setContentLength(ciphertext.getBytes(StandardCharsets.UTF_8).length);
            response.getWriter().write(ciphertext);
        }
    }

    private boolean matches(String requestUri, Set<String> paths) {
        for (String pattern : paths) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * 包装 HttpServletRequest，将解密后的 JSON 作为新的 body
     */
    private static class DecryptRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] body;

        DecryptRequestWrapper(HttpServletRequest request, String decryptedJson) {
            super(request);
            this.body = decryptedJson.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public int read() { return bais.read(); }
                @Override
                public boolean isFinished() { return bais.available() == 0; }
                @Override
                public boolean isReady() { return true; }
                @Override
                public void setReadListener(ReadListener listener) {}
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body), StandardCharsets.UTF_8));
        }

        @Override
        public int getContentLength() { return body.length; }

        @Override
        public long getContentLengthLong() { return body.length; }

        @Override
        public String getContentType() { return "application/json"; }
    }

    /**
     * 包装 HttpServletResponse，捕获所有输出字节
     */
    private static class ByteArrayResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream capture = new ByteArrayOutputStream();
        private ServletOutputStream captureStream;
        private PrintWriter captureWriter;

        ByteArrayResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (captureWriter != null) {
                throw new IllegalStateException("getWriter() already called");
            }
            if (captureStream == null) {
                captureStream = new ServletOutputStream() {
                    @Override
                    public void write(int b) { capture.write(b); }
                    @Override
                    public boolean isReady() { return true; }
                    @Override
                    public void setWriteListener(WriteListener listener) {}
                };
            }
            return captureStream;
        }

        @Override
        public PrintWriter getWriter() {
            if (captureStream != null) {
                throw new IllegalStateException("getOutputStream() already called");
            }
            if (captureWriter == null) {
                captureWriter = new PrintWriter(new OutputStreamWriter(capture, StandardCharsets.UTF_8));
            }
            return captureWriter;
        }

        byte[] getCapturedBytes() {
            if (captureWriter != null) {
                captureWriter.flush();
            }
            return capture.toByteArray();
        }
    }
}
