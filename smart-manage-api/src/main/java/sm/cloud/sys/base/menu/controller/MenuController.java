package sm.cloud.sys.base.menu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.cloud.sys.base.menu.domain.form.MenuListForm;
import sm.cloud.sys.base.menu.domain.form.MenuSaveForm;
import sm.cloud.sys.base.menu.domain.form.MenuSelectForm;
import sm.cloud.sys.base.menu.domain.form.UserMenusByAppIdForm;
import sm.cloud.sys.base.menu.domain.form.UserMenusByAppNumberForm;
import sm.cloud.sys.base.menu.domain.vo.MenuCreateNewDataVO;
import sm.cloud.sys.base.menu.domain.vo.MenuDetailVO;
import sm.cloud.sys.base.menu.domain.vo.MenuListVO;
import sm.cloud.sys.base.menu.domain.vo.MenuSelectVO;
import sm.cloud.sys.base.menu.domain.vo.MenuVO;
import sm.cloud.sys.base.menu.service.MenuService;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.form.IdForm;
import sm.system.response.PageResult;
import sm.system.response.Result;

/**
 * 菜单管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "菜单管理", description = "菜单信息管理接口")
@RequiredArgsConstructor
public class MenuController {
	private final MenuService service;

	@Operation(summary = "菜单列表", description = "获取菜单分页列表数据")
	@PostMapping("/sys/base/menu/listPage")
	@SaCheckPermission("sys:base:menu:listPage")
	public Result<PageResult<MenuListVO>> listPage(@RequestBody MenuListForm form) {
		return Result.success(service.listPage(form));
	}

	@Operation(summary = "菜单选择", description = "基础资料选择：获取菜单分页列表数据")
	@PostMapping("/sys/base/menu/select")
	@SaCheckPermission("sys:base:menu:select")
	public Result<PageResult<MenuSelectVO>> select(@RequestBody MenuSelectForm form) {
		return Result.success(service.select(form));
	}

	@Operation(summary = "用户菜单", description = "获取当前用户在应用下的菜单树")
	@PostMapping("/sys/base/menu/getUserMenusByAppId")
	public Result<MenuVO> getUserMenusByAppId(@RequestBody @Valid UserMenusByAppIdForm form) {
		return Result.success(service.getUserMenusByAppId(UserHelper.getCurrentUserId(), form.getAppId()));
	}

	@Operation(summary = "用户菜单（按应用编号）", description = "获取当前用户在应用下的菜单树（按 t_auth_app.number）")
	@PostMapping("/sys/base/menu/getUserMenusByAppNumber")
	public Result<MenuVO> getUserMenusByAppNumber(@RequestBody @Valid UserMenusByAppNumberForm form) {
		return Result.success(service.getUserMenusByAppNumber(UserHelper.getCurrentUserId(), form.getNumber()));
	}

	@Operation(summary = "菜单详情", description = "按ID查询菜单")
	@SaCheckPermission("sys:base:menu:detail")
	@PostMapping("/sys/base/menu/detail")
	public Result<MenuDetailVO> detail(@RequestBody @Valid IdForm form) {
		return Result.success(service.getDetail(form.getId()));
	}

	@PostMapping("/sys/base/menu/save")
	@Operation(summary = "保存菜单", description = "新增或更新菜单")
	@SaCheckPermission("sys:base:menu:save")
	public Result<Long> save(@Valid @RequestBody MenuSaveForm form) {
		return Result.success(service.save(form));
	}

	@GetMapping("/sys/base/menu/createNewData")
	@Operation(summary = "获取新增默认值", description = "获取菜单新增时的默认初始数据")
	@SaCheckPermission("sys:base:menu:save")
	public Result<MenuCreateNewDataVO> createNewData() {
		return Result.success(service.createNewData());
	}

	@PostMapping("/sys/base/menu/delete")
	@Operation(summary = "删除菜单", description = "按ID删除菜单")
	@SaCheckPermission("sys:base:menu:delete")
	public Result<String> delete(@RequestBody @Valid IdForm form) {
		service.deleteById(form.getId());
		return Result.success();
	}
}
