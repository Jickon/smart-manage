package sm.domain.sys.base.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import sm.domain.sys.base.menu.model.vo.MenuVO;

import java.time.LocalDateTime;
import java.util.List;

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

	private String email;

	private String phone;

	private Boolean enabled;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

	private Integer version;

	@Schema(description = "当前组织下的角色ID列表")
	private List<Long> roleIds;

	@Schema(description = "菜单")
	private MenuVO menus;

}
