package sm.cloud.sys.base.roleperms.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色权限分配表单
 *
 * @author Chekfu
 */
@Data
@Schema(description = "角色权限分配表单")
public class RolePermsSaveForm {

	@NotNull(message = "角色ID不能为空")
	@Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long roleId;

	@Schema(description = "权限ID列表")
	private List<Long> permissionIds;
}
