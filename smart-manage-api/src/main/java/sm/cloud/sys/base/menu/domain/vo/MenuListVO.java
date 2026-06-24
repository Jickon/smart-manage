package sm.cloud.sys.base.menu.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import sm.cloud.sys.base.common.enums.MenuLevelEnum;

/**
 * @author Chekfu
 */
@Data
@Schema(title = "菜单列表视图")
public class MenuListVO {
	@Schema(description = "id")
	private Long id;

	@Schema(description = "编号")
	private String number;

	@Schema(description = "菜单层级")
	private MenuLevelEnum level;

	@Schema(description = "父菜单ID，一级菜单为0")
	private Long parentId;

	@Schema(description = "菜单名称")
	private String name;

	@Schema(description = "菜单路径")
	private String path;

	@Schema(description = "组件路径")
	private String component;

	@Schema(description = "菜单排序")
	private Integer sort;

	@Schema(description = "图标")
	private String icon;
}
