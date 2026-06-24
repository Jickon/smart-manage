package sm.domain.sys.base.permission.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chekfu
 */
@Data
@Schema(title = "权限列表视图")
public class PermissionListVO {
	@Schema(description = "id")
	private Long id;

	@Schema(description = "权限名称")
	private String name;

	@Schema(description = "权限编码")
	private String number;

	@Schema(description = "应用ID")
	private Long appId;
}
