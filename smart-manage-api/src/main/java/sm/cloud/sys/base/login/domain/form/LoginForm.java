package sm.cloud.sys.base.login.domain.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Chekfu
 */
@Data
public class LoginForm {
	@NotBlank(message = "用户名不能为空")
	private String username;

	@NotBlank(message = "密码不能为空")
	private String password;

	@NotBlank(message = "验证码不能为空")
	private String captcha;

	@NotBlank(message = "验证码ID不能为空")
	private String captchaId;
}