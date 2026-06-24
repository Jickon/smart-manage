package sm.system.helper;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sm.system.constant.SysConstant;

/**
 * @author Chekfu
 */
@Component
public class EnvironmentHelper {
	@Getter
	private static String environment;

	@Value("${spring.profiles.active}")
	private String environmentValue;

	@PostConstruct
	public void init() {
		environment = environmentValue;
	}

	public static boolean isProd() {
		return SysConstant.ENVIRONMENT_PROD.equals(environment);
	}

}
