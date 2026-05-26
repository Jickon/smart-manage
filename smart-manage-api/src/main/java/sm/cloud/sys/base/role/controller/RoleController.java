package sm.cloud.sys.base.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.cloud.sys.base.role.domain.form.RoleListForm;
import sm.cloud.sys.base.role.domain.form.RoleSaveForm;
import sm.cloud.sys.base.role.domain.form.RoleSelectForm;
import sm.cloud.sys.base.role.domain.vo.RoleCreateNewDataVO;
import sm.cloud.sys.base.role.domain.vo.RoleDetailVO;
import sm.cloud.sys.base.role.domain.vo.RoleListVO;
import sm.cloud.sys.base.role.domain.vo.RoleSelectVO;
import sm.cloud.sys.base.role.service.RoleService;
import sm.system.aop.log.BizLog;
import sm.system.form.IdForm;
import sm.system.response.PageResult;
import sm.system.response.Result;

/**
 * 角色管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "角色管理", description = "角色信息管理接口")
@RequiredArgsConstructor
public class RoleController {
	private final RoleService service;

	@Operation(summary = "角色列表", description = "获取角色分页列表数据")
	@PostMapping("/sys/base/role/listPage")
	@SaCheckPermission("sys:base:role:listPage")
	@BizLog("角色分页查询")
	public Result<PageResult<RoleListVO>> listPage(@RequestBody RoleListForm form) {
		return Result.success(service.listPage(form));
	}

	@Operation(summary = "角色选择", description = "基础资料选择：获取角色分页列表数据")
	@PostMapping("/sys/base/role/select")
	@SaCheckPermission("sys:base:role:select")
	public Result<PageResult<RoleSelectVO>> select(@RequestBody RoleSelectForm form) {
		return Result.success(service.select(form));
	}

	@Operation(summary = "角色详情", description = "根据ID获取角色详情")
	@SaCheckPermission("sys:base:role:detail")
	@PostMapping("/sys/base/role/detail")
	public Result<RoleDetailVO> detail(@RequestBody @Valid IdForm form) {
		return Result.success(service.getDetail(form.getId()));
	}

	@Operation(summary = "保存角色", description = "新增或更新角色")
	@PostMapping("/sys/base/role/save")
	@SaCheckPermission("sys:base:role:save")
	public Result<Long> save(@Valid @RequestBody RoleSaveForm form) {
		return Result.success(service.save(form));
	}

	@GetMapping("/sys/base/role/createNewData")
	@Operation(summary = "获取新增默认值", description = "获取角色新增时的默认初始数据")
	@SaCheckPermission("sys:base:role:save")
	public Result<RoleCreateNewDataVO> createNewData() {
		return Result.success(service.createNewData());
	}

	@Operation(summary = "删除角色", description = "根据ID删除角色")
	@PostMapping("/sys/base/role/delete")
	@SaCheckPermission("sys:base:role:delete")
	public Result<String> delete(@RequestBody IdForm form) {
		service.deleteById(form.getId());
		return Result.success();
	}
}
