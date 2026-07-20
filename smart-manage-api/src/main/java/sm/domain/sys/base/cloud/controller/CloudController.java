package sm.domain.sys.base.cloud.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.domain.sys.base.cloud.model.form.CloudListForm;
import sm.domain.sys.base.cloud.model.form.CloudSelectForm;
import sm.domain.sys.base.cloud.model.form.CloudSaveForm;
import sm.domain.sys.base.cloud.model.vo.CloudCreateNewDataVO;
import sm.domain.sys.base.cloud.model.vo.CloudDetailVO;
import sm.domain.sys.base.cloud.model.vo.CloudListVO;
import sm.domain.sys.base.cloud.model.vo.CloudSelectVO;
import sm.domain.sys.base.cloud.service.CloudService;
import sm.system.form.IdForm;
import sm.system.form.IdsForm;
import sm.system.response.PageData;
import sm.system.response.Result;

@RestController
@Tag(name = "系统建模-云管理", description = "云信息管理接口")
@RequiredArgsConstructor
public class CloudController {
	private final CloudService service;

	@Operation(summary = "云列表", description = "获取云分页列表数据")
	@PostMapping("/sys/base/cloud/listPage")
	@SaCheckPermission("sys:base:cloud:listPage")
	public Result<PageData<CloudListVO>> listPage(@RequestBody CloudListForm form) {
		return Result.success(service.listPage(form));
	}

	@Operation(summary = "云选择", description = "基础资料选择：获取云分页列表数据")
	@PostMapping("/sys/base/cloud/select")
	@SaCheckPermission("sys:base:cloud:select")
	public Result<PageData<CloudSelectVO>> select(@RequestBody CloudSelectForm form) {
		return Result.success(service.select(form));
	}

	@Operation(summary = "云详情", description = "按ID查询云")
	@PostMapping("/sys/base/cloud/detail")
	@SaCheckPermission("sys:base:cloud:detail")
	public Result<CloudDetailVO> detail(@RequestBody @Valid IdForm form) {
		return Result.success(service.getDetail(form.getId()));
	}

	@Operation(summary = "保存云", description = "新增或更新云")
	@PostMapping("/sys/base/cloud/save")
	@SaCheckPermission("sys:base:cloud:save")
	public Result<Long> save(@Valid @RequestBody CloudSaveForm form) {
		return Result.success(service.save(form));
	}

	@GetMapping("/sys/base/cloud/createNewData")
	@Operation(summary = "获取新增默认值", description = "获取云新增时的默认初始数据")
	@SaCheckPermission("sys:base:cloud:save")
	public Result<CloudCreateNewDataVO> createNewData() {
		return Result.success(service.createNewData());
	}

	@Operation(summary = "删除云", description = "按ID删除云")
	@PostMapping("/sys/base/cloud/delete")
	@SaCheckPermission("sys:base:cloud:delete")
	public Result<String> delete(@RequestBody @Valid IdForm form) {
		service.deleteById(form.getId());
		return Result.success();
	}

	@PostMapping("/sys/base/cloud/enable")
	@SaCheckPermission("sys:base:cloud:enable")
	public Result<String> enable(@RequestBody @Valid IdsForm form) {
		service.enable(form.getIds());
		return Result.success();
	}

	@PostMapping("/sys/base/cloud/disable")
	@SaCheckPermission("sys:base:cloud:disable")
	public Result<String> disable(@RequestBody @Valid IdsForm form) {
		service.disable(form.getIds());
		return Result.success();
	}
}

