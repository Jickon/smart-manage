package sm.domain.sys.base.user.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author Chekfu
 */
@Data
@Schema(description = "用户保存表单")
public class UserSaveForm {

	@Schema(description = "id")
	private Long id;

	@Schema(description = "乐观锁版本号，修改时必传")
	private Integer version;

	@Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "用户名不能为空")
	private String username;

	@Schema(description = "密码，为空则不修改")
	private String password;

	@Schema(description = "昵称")
	private String nickname;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "手机号")
	private String phone;

	@Schema(description = "头像地址")
	private String avatar;

	@Schema(description = "主题色")
	private String themeColor;

}
