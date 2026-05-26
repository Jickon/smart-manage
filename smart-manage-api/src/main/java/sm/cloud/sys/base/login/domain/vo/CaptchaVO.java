package sm.cloud.sys.base.login.domain.vo;

import lombok.Data;

/**
 * 验证码返回VO
 *
 * @author Chekfu
 */
@Data
public class CaptchaVO {
	private String captchaId;
	private String imageData;
}
