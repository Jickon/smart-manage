package sm.domain.sys.base.roleperms.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色权限视图
 *
 * @author Chekfu
 */
@Data
@Schema(description = "角色权限视图")
public class RolePermsVO {

	@Schema(description = "关联ID")
	private Long id;

	@Schema(description = "角色ID")
	private Long roleId;

	@Schema(description = "权限ID")
	private Long permissionId;

	@Schema(description = "权限名称")
	private String permName;

	@Schema(description = "权限编码")
	private String permNumber;
}
