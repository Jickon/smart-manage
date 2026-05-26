package sm.cloud.sys.base.role.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色保存表单
 *
 * @author Chekfu
 */
@Data
@Schema(description = "角色保存表单")
public class RoleSaveForm {

	@Schema(description = "id，为空则新增")
	private Long id;

	@Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "角色名称不能为空")
	private String name;

	@Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "角色编码不能为空")
	private String number;

	@Schema(description = "备注")
	private String remark;
}
