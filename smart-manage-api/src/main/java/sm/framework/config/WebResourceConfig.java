package sm.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 资源映射配置
 *
 * @author Chekfu
 */
@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Value("${smart-manage.system.upload.dir:E:/upload/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录路径以 / 结尾
        String dir = uploadDir;
        if (!dir.endsWith("/") && !dir.endsWith("\\")) {
            dir = dir + "/";
        }
        // 映射 /upload/** 到上传目录
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + dir);
    }
}
