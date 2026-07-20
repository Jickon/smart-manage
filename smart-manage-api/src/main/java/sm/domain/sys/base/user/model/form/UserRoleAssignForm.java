package sm.domain.sys.base.user.model.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 用户角色分配命令。 */
@Data
public class UserRoleAssignForm {
	@NotNull(message = "用户ID不能为空")
	private Long userId;

	@NotNull(message = "角色列表不能为空")
	private List<Long> roleIds;
}
