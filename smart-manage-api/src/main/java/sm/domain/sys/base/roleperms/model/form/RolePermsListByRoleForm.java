package sm.domain.sys.base.roleperms.model.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 获取角色权限列表入参
 */
@Data
public class RolePermsListByRoleForm {
	@NotNull(message = "roleId不能为空")
	private Long roleId;
}

