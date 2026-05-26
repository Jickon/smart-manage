package sm.cloud.sys.base.roleperms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.cloud.sys.base.roleperms.domain.form.RolePermsListByRoleForm;
import sm.cloud.sys.base.roleperms.domain.form.RolePermsSaveForm;
import sm.cloud.sys.base.roleperms.domain.vo.RolePermsVO;
import sm.cloud.sys.base.roleperms.service.RolePermsService;
import sm.system.form.IdForm;
import sm.system.response.Result;

import java.util.List;

/**
 * 角色权限管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "角色权限管理", description = "角色权限关联管理接口")
@RequiredArgsConstructor
public class RolePermsController {
	private final RolePermsService service;

	@Operation(summary = "角色权限列表", description = "获取角色的权限列表")
	@SaCheckPermission("sys:base:roleperms:list")
	@PostMapping("/sys/base/roleperms/listByRole")
	public Result<List<RolePermsVO>> listByRole(@RequestBody @Valid RolePermsListByRoleForm form) {
		return Result.success(service.getRolePermissions(form.getRoleId()));
	}

	@Operation(summary = "保存角色权限", description = "批量分配角色权限")
	@PostMapping("/sys/base/roleperms/save")
	@SaCheckPermission("sys:base:roleperms:save")
	public Result<String> save(@Valid @RequestBody RolePermsSaveForm form) {
		service.save(form);
		return Result.success();
	}

	@Operation(summary = "删除角色权限", description = "删除角色权限关联")
	@PostMapping("/sys/base/roleperms/delete")
	@SaCheckPermission("sys:base:roleperms:delete")
	public Result<String> delete(@RequestBody IdForm form) {
		service.deleteById(form.getId());
		return Result.success();
	}
}
