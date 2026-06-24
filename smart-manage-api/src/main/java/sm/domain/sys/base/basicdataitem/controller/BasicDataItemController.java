package sm.domain.sys.base.basicdataitem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.domain.sys.base.basicdataitem.model.form.BasicDataItemSaveForm;
import sm.domain.sys.base.basicdataitem.model.vo.BasicDataItemListVO;
import sm.domain.sys.base.basicdataitem.model.vo.BasicDataOptionVO;
import sm.domain.sys.base.basicdataitem.service.BasicDataItemService;
import sm.system.form.IdForm;
import sm.system.response.Result;

import java.util.List;

/**
 * 基础数据项管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "系统建模-基础数据管理", description = "基础数据项管理接口")
@RequiredArgsConstructor
public class BasicDataItemController {
    private final BasicDataItemService service;

    @PostMapping("/sys/base/basic-data-item/listByTypeNumber")
    @Operation(summary = "基础数据项列表", description = "按基础数据编码查询项列表")
    @SaCheckPermission("sys:base:basic-data:listPage")
    public Result<List<BasicDataItemListVO>> listByTypeNumber(@RequestBody @Valid TypeNumberForm form) {
        return Result.success(service.listByTypeNumber(form.getTypeNumber()));
    }

    @PostMapping("/sys/base/basic-data-item/save")
    @Operation(summary = "保存基础数据项", description = "新增或更新基础数据项")
    @SaCheckPermission("sys:base:basic-data:save")
    public Result<Long> save(@Valid @RequestBody BasicDataItemSaveForm form) {
        return Result.success(service.save(form));
    }

    @PostMapping("/sys/base/basic-data-item/delete")
    @Operation(summary = "删除基础数据项", description = "按ID删除基础数据项")
    @SaCheckPermission("sys:base:basic-data:delete")
    public Result<String> delete(@RequestBody @Valid IdForm form) {
        service.deleteById(form.getId());
        return Result.success();
    }

    // ==================== 消费端接口（供前端下拉框使用） ====================

    @PostMapping("/sys/base/basic-data/items")
    @Operation(summary = "基础数据选项", description = "按基础数据编码获取选项列表，供下拉框等组件使用")
    public Result<List<BasicDataOptionVO>> items(@RequestBody @Valid TypeNumberForm form) {
        return Result.success(service.getOptionsByTypeNumber(form.getTypeNumber()));
    }

    /**
     * 基础数据项查询表单
     */
    @lombok.Data
    public static class TypeNumberForm {
        @jakarta.validation.constraints.NotBlank(message = "基础数据编码不能为空")
        @io.swagger.v3.oas.annotations.media.Schema(description = "基础数据编码")
        private String typeNumber;
    }
}
