package sm.domain.sys.base.userrole.model.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 获取用户在指定组织下的角色列表入参
 */
@Data
public class UserRoleListByUserForm {
	@NotNull(message = "userId不能为空")
	private Long userId;

	@NotNull(message = "orgId不能为空")
	private Long orgId;
}

