package sm.cloud.sys.base.user.service;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sm.cloud.sys.base.permission.service.PermissionService;
import sm.cloud.sys.common.helper.UserHelper;

import java.util.List;

/**
 * 自定义权限加载接口实现类
 *
 * @author Chekfu
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {
	private final PermissionService permissionService;

	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		if (UserHelper.isAdmin()) {
			return List.of("*");
		}
		long uid = Long.parseLong(String.valueOf(loginId));
		Long orgId = UserHelper.getCurrentOrgId();
		return permissionService.getUserPermissions(uid, orgId);
	}

	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		return List.of();
	}
}
