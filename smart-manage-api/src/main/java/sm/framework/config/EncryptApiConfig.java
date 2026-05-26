package sm.framework.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import sm.system.aop.encrypt.DecryptApi;
import sm.system.aop.encrypt.EncryptApi;
import sm.system.filter.EncryptApiFilter;
import sm.system.helper.SM4Helper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 接口加解密配置：扫描 @EncryptApi / @DecryptApi 注解，注册加解密过滤器
 *
 * @author Chekfu
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class EncryptApiConfig {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final SM4Helper sm4Helper;

    @Bean
    public Set<String> encryptApiPaths() {
        return scanPaths(EncryptApi.class);
    }

    @Bean
    public Set<String> decryptApiPaths() {
        return scanPaths(DecryptApi.class);
    }

    @Bean
    public FilterRegistrationBean<EncryptApiFilter> encryptApiFilter() {
        EncryptApiFilter filter = new EncryptApiFilter(sm4Helper, encryptApiPaths(), decryptApiPaths());
        FilterRegistrationBean<EncryptApiFilter> bean = new FilterRegistrationBean<>(filter);
        bean.addUrlPatterns("/*");
        // 在 SaToken 认证过滤器之后执行，确保认证错误也能被加密
        bean.setOrder(0);
        log.info("SM4 接口加解密已启用，加密路径: {}，解密路径: {}", encryptApiPaths().size(), decryptApiPaths().size());
        return bean;
    }

    /** 扫描所有标注了指定注解的 Controller 方法，返回路径集合 */
    private Set<String> scanPaths(Class<? extends java.lang.annotation.Annotation> annotationClass) {
        Set<String> paths = new HashSet<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();

            // 方法级别优先，其次类级别
            boolean hasAnnotation = handlerMethod.getMethodAnnotation(annotationClass) != null;
            if (!hasAnnotation) {
                hasAnnotation = handlerMethod.getBeanType().getAnnotation(annotationClass) != null;
            }
            if (!hasAnnotation) {
                continue;
            }

            PathPatternsRequestCondition pathPatternsCondition = entry.getKey().getPathPatternsCondition();
            if (pathPatternsCondition != null) {
                paths.addAll(pathPatternsCondition.getPatternValues());
            }
        }
        return paths;
    }
}
