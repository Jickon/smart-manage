package sm.domain.sys.base.userrole.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户角色视图
 *
 * @author Chekfu
 */
@Data
@Schema(description = "用户角色视图")
public class UserRoleVO {

	@Schema(description = "关联ID")
	private Long id;

	@Schema(description = "用户ID")
	private Long userId;

	@Schema(description = "组织ID")
	private Long orgId;

	@Schema(description = "角色ID")
	private Long roleId;

	@Schema(description = "角色名称")
	private String roleName;

	@Schema(description = "角色编码")
	private String roleNumber;
}
