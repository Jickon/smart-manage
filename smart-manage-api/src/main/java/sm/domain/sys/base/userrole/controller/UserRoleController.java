package sm.domain.sys.base.userrole.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.domain.sys.base.userrole.model.form.UserRoleListByCurrentOrgUserForm;
import sm.domain.sys.base.userrole.model.form.UserRoleListByUserForm;
import sm.domain.sys.base.userrole.model.form.UserRoleSaveForm;
import sm.domain.sys.base.userrole.model.vo.UserRoleVO;
import sm.domain.sys.base.userrole.service.UserRoleService;
import sm.system.form.IdForm;
import sm.system.response.Result;

import java.util.List;

/**
 * 用户角色管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "用户角色管理", description = "用户角色关联管理接口")
@RequiredArgsConstructor
public class UserRoleController {
	private final UserRoleService service;

	@Operation(summary = "用户角色列表-当前组织", description = "获取用户在当前组织下的角色列表，组织由服务端上下文决定")
	@SaCheckPermission("sys:base:userrole:list")
	@PostMapping("/sys/base/userrole/listByCurrentOrgUser")
	public Result<List<UserRoleVO>> listByCurrentOrgUser(@RequestBody @Valid UserRoleListByCurrentOrgUserForm form) {
		return Result.success(service.getUserRolesByCurrentOrg(form.getUserId()));
	}

	@Operation(summary = "用户角色列表", description = "获取用户在指定组织下的角色列表")
	@SaCheckPermission("sys:base:userrole:list")
	@PostMapping("/sys/base/userrole/listByUser")
	public Result<List<UserRoleVO>> listByUser(@RequestBody @Valid UserRoleListByUserForm form) {
		return Result.success(service.getUserRoles(form.getUserId(), form.getOrgId()));
	}

	@Operation(summary = "保存用户角色", description = "批量分配用户角色")
	@PostMapping("/sys/base/userrole/save")
	@SaCheckPermission("sys:base:userrole:save")
	public Result<String> save(@Valid @RequestBody UserRoleSaveForm form) {
		service.save(form);
		return Result.success();
	}

	@Operation(summary = "删除用户角色", description = "删除用户角色关联")
	@PostMapping("/sys/base/userrole/delete")
	@SaCheckPermission("sys:base:userrole:delete")
	public Result<String> delete(@RequestBody @Valid IdForm form) {
		service.deleteById(form.getId());
		return Result.success();
	}
}
