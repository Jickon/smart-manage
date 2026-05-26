package sm.cloud.sys.base.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chekfu
 */
@Data
@Schema(description = "用户列表视图")
public class UserListVO {
	@Schema(description = "id")
	private Long id;

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "昵称")
	private String nickname;

	@Schema(description = "头像地址")
	private String avatar;
}
