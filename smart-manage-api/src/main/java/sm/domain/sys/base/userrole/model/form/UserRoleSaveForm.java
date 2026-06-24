package sm.domain.sys.base.userrole.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户角色分配表单
 *
 * @author Chekfu
 */
@Data
@Schema(description = "用户角色分配表单")
public class UserRoleSaveForm {

	@NotNull(message = "用户ID不能为空")
	@Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long userId;

	@NotNull(message = "组织ID不能为空")
	@Schema(description = "组织ID", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long orgId;

	@Schema(description = "角色ID列表")
	private List<Long> roleIds;
}
