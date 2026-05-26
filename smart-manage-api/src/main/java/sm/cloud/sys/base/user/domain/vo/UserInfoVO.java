package sm.cloud.sys.base.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import sm.cloud.sys.base.menu.domain.vo.MenuVO;

/**
 * 用户信息面板
 *
 * @author Chekfu
 */
@Data
@Schema(title = "用户信息面板")
public class UserInfoVO {

	@Schema(description = "用户 id")
	private Long id;

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "姓名")
	private String nickname;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "主题颜色")
	private String themeColor;

	@Schema(description = "菜单")
	private MenuVO menus;

}
