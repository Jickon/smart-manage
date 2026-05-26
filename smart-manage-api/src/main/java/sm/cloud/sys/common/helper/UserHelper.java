package sm.cloud.sys.common.helper;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sm.cloud.sys.base.user.domain.entity.UserEntity;
import sm.cloud.sys.base.user.service.UserService;
import sm.cloud.sys.common.constat.UserConst;

/**
 * @author Chekfu
 */
@Component
public class UserHelper {
	private static UserService userService;
	private static Long defaultOrgId = 1L;

	@Autowired
	public void setUserService(UserService userService) {
		UserHelper.userService = userService;
	}

	@Value("${smart-manage.org.default-id:1}")
	public void setDefaultOrgId(Long val) {
		UserHelper.defaultOrgId = val;
	}

	/**
	 * 获取当前登录用户ID
	 */
	public static Long getCurrentUserId() {
		return StpUtil.getLoginIdAsLong();
	}

	/**
	 * 获取当前登录用户组织ID
	 */
	public static Long getCurrentOrgId() {
		Long v = StpUtil.getTokenSession().getLong("orgId");
		return v != null ? v : defaultOrgId;
	}

	/**
	 * 设置当前登录用户组织ID
	 */
	public static void setCurrentOrgId(Long orgId) {
		StpUtil.getTokenSession().set("orgId", orgId);
	}

	/**
	 * 获取当前登录用户信息
	 */
	public static UserEntity getCurrentUser() {
		return userService.getById(getCurrentUserId());
	}

	/**
	 * 判断是否已登�?
	 */
	public static boolean isLogin() {
		return StpUtil.isLogin();
	}

	/**
	 * 获取当前登录用户的token
	 */
	public static String getToken() {
		return StpUtil.getTokenValue();
	}

	/**
	 * 判断当前用户是否是管理员
	 */
	public static boolean isAdmin() {
		if (!isLogin()) {
			return false;
		}
		UserEntity user = getCurrentUser();
		if (user == null) {
			return false;
		}
		return UserConst.SUPER_ADMIN.equals(user.getUsername());
	}
}