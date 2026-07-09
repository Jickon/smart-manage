package sm.domain.sys.base.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.domain.sys.base.user.model.form.UserListForm;
import sm.domain.sys.base.user.model.form.UserPermissionsForm;
import sm.domain.sys.base.user.model.form.UserSaveForm;
import sm.domain.sys.base.user.model.vo.UserCreateNewDataVO;
import sm.domain.sys.base.user.model.vo.UserInfoVO;
import sm.domain.sys.base.user.model.vo.UserListVO;
import sm.domain.sys.base.user.service.UserService;
import sm.system.exception.BizException;
import sm.system.form.IdForm;
import sm.system.response.PageData;
import sm.system.response.Result;
import sm.system.response.ResultEnum;
import sm.system.util.BeanUtil;

import java.util.List;

/**
 * 用户管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "用户管理", description = "用户信息管理接口")
@RequiredArgsConstructor
public class UserController {
	private final UserService service;

	@PostMapping("/sys/base/user/listPage")
	@Operation(summary = "用户列表", description = "获取用户分页列表数据")
	@SaCheckPermission("sys:base:user:listPage")
	public Result<PageData<UserListVO>> listPage(@RequestBody UserListForm form) {
		return Result.success(service.listPage(form));
	}

	@GetMapping("/sys/base/user/current")
	@Operation(summary = "用户信息", description = "获取当前登录用户信息")
	public Result<UserInfoVO> current() {
		return Result.success(service.current());
	}

	@PostMapping("/sys/base/user/permissions")
	@Operation(summary = "用户权限", description = "按前缀获取当前用户权限编码列表")
	public Result<List<String>> permissions(@RequestBody @Valid UserPermissionsForm form) {
		return Result.success(service.permissions(form.getPrefix()));
	}

	@Operation(summary = "用户详情", description = "按ID查询用户")
	@SaCheckPermission("sys:base:user:detail")
	@PostMapping("/sys/base/user/detail")
	public Result<UserInfoVO> detail(@RequestBody @Valid IdForm form) {
		var user = service.getById(form.getId());
		if (user == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "用户不存在");
		}
		return Result.success(BeanUtil.copyProperties(user, UserInfoVO.class));
	}

	@PostMapping("/sys/base/user/save")
	@Operation(summary = "保存用户", description = "新增或更新用户")
	@SaCheckPermission("sys:base:user:save")
	public Result<Long> saveUser(@RequestBody @Valid UserSaveForm form) {
		return Result.success(service.save(form));
	}

	@PostMapping("/sys/base/user/delete")
	@Operation(summary = "删除用户", description = "按ID删除用户")
	@SaCheckPermission("sys:base:user:delete")
	public Result<String> deleteUser(@RequestBody @Valid IdForm form) {
		service.deleteById(form.getId());
		return Result.success();
	}

	@GetMapping("/sys/base/user/createNewData")
	@Operation(summary = "获取新增默认值", description = "获取用户新增时的默认初始数据")
	@SaCheckPermission("sys:base:user:save")
	public Result<UserCreateNewDataVO> createNewData() {
		return Result.success(service.createNewData());
	}
}
