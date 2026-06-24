package sm.domain.sys.base.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chekfu
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "smart-manage.captcha")
public class CaptchaConfig {
	private int width;
	private int height;
	private int length;
	private int expire;
}