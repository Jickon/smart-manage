package sm.domain.sys.base.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.domain.sys.base.permission.model.form.PermissionListForm;
import sm.domain.sys.base.permission.model.form.PermissionSaveForm;
import sm.domain.sys.base.permission.model.form.PermissionSelectForm;
import sm.domain.sys.base.permission.model.vo.PermissionCreateNewDataVO;
import sm.domain.sys.base.permission.model.vo.PermissionDetailVO;
import sm.domain.sys.base.permission.model.vo.PermissionListVO;
import sm.domain.sys.base.permission.model.vo.PermissionSelectVO;
import sm.domain.sys.base.permission.service.PermissionService;
import sm.system.form.IdForm;
import sm.system.response.PageData;
import sm.system.response.Result;

/**
 * 权限管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "权限管理", description = "权限信息管理接口")
@RequiredArgsConstructor
public class PermissionController {
	private final PermissionService service;

	@Operation(summary = "权限列表", description = "获取权限分页列表数据")
	@PostMapping("/sys/base/permission/listPage")
	@SaCheckPermission("sys:base:permission:listPage")
	public Result<PageData<PermissionListVO>> listPage(@RequestBody PermissionListForm form) {
		return Result.success(service.listPage(form));
	}

	@Operation(summary = "权限选择", description = "基础资料选择：获取权限分页列表数据")
	@PostMapping("/sys/base/permission/select")
	@SaCheckPermission("sys:base:permission:select")
	public Result<PageData<PermissionSelectVO>> select(@RequestBody PermissionSelectForm form) {
		return Result.success(service.select(form));
	}

	@Operation(summary = "权限详情", description = "按ID查询权限")
	@SaCheckPermission("sys:base:permission:detail")
	@PostMapping("/sys/base/permission/detail")
	public Result<PermissionDetailVO> detail(@RequestBody @Valid IdForm form) {
		return Result.success(service.getDetail(form.getId()));
	}

	@PostMapping("/sys/base/permission/save")
	@Operation(summary = "保存权限", description = "新增或更新权限")
	@SaCheckPermission("sys:base:permission:save")
	public Result<Long> save(@Valid @RequestBody PermissionSaveForm form) {
		return Result.success(service.save(form));
	}

	@GetMapping("/sys/base/permission/createNewData")
	@Operation(summary = "获取新增默认值", description = "获取权限新增时的默认初始数据")
	@SaCheckPermission("sys:base:permission:save")
	public Result<PermissionCreateNewDataVO> createNewData() {
		return Result.success(service.createNewData());
	}

	@PostMapping("/sys/base/permission/delete")
	@Operation(summary = "删除权限", description = "按ID删除权限")
	@SaCheckPermission("sys:base:permission:delete")
	public Result<String> delete(@RequestBody @Valid IdForm form) {
		service.deleteById(form.getId());
		return Result.success();
	}
}
