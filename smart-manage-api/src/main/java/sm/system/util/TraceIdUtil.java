package sm.system.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

/**
 * TraceId工具类
 *
 * @author Chekfu
 */
public class TraceIdUtil {

	public static final String TRACE_ID_STRING = "traceId";

	private static final InheritableThreadLocal<String> TRACE_ID = new InheritableThreadLocal<>();

	public static String generateTraceId(HttpServletRequest request) {
		String header = request.getHeader(TRACE_ID_STRING);
		if (header != null) {
			return header;
		}
		return UUID.randomUUID().toString();
	}

	public static String getTraceId() {
		return TRACE_ID.get();
	}

	public static void setTraceId(HttpServletRequest request) {
		String traceId = getTraceId();
		if (traceId == null) {
			traceId = generateTraceId(request);
			TRACE_ID.set(traceId);
		}
	}

	public static void clear() {
		TRACE_ID.remove();
	}
}
