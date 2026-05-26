package sm.cloud.sys.monitor.script.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.cloud.sys.monitor.script.domain.form.ScriptExecuteForm;
import sm.cloud.sys.monitor.script.domain.form.ScriptListForm;
import sm.cloud.sys.monitor.script.domain.form.ScriptSaveForm;
import sm.cloud.sys.monitor.script.domain.vo.ScriptDetailVO;
import sm.cloud.sys.monitor.script.domain.vo.ScriptListVO;
import sm.cloud.sys.monitor.script.domain.vo.ScriptResultVO;
import sm.cloud.sys.monitor.script.service.ScriptService;
import sm.system.form.IdForm;
import sm.system.response.PageResult;
import sm.system.response.Result;

/**
 * 脚本控制台
 */
@RestController
@Tag(name = "脚本控制台", description = "JS 脚本执行与管理")
@RequiredArgsConstructor
public class ScriptController {

    private final ScriptService scriptService;

    @PostMapping("/sys/monitor/script/execute")
    @Operation(summary = "执行 JS 脚本")
    @SaCheckPermission("sys:monitor:script:execute")
    public Result<ScriptResultVO> execute(@Valid @RequestBody ScriptExecuteForm form) {
        return Result.success(scriptService.execute(form));
    }

    @PostMapping("/sys/monitor/script/listPage")
    @Operation(summary = "脚本分页查询")
    @SaCheckPermission("sys:monitor:script:listPage")
    public Result<PageResult<ScriptListVO>> listPage(@Valid @RequestBody ScriptListForm form) {
        return Result.success(scriptService.listPage(form));
    }

    @PostMapping("/sys/monitor/script/detail")
    @Operation(summary = "脚本详情")
    @SaCheckPermission("sys:monitor:script:detail")
    public Result<ScriptDetailVO> detail(@Valid @RequestBody IdForm form) {
        return Result.success(scriptService.detail(form.getId()));
    }

    @PostMapping("/sys/monitor/script/save")
    @Operation(summary = "保存脚本")
    @SaCheckPermission("sys:monitor:script:save")
    public Result<Long> save(@Valid @RequestBody ScriptSaveForm form) {
        return Result.success(scriptService.save(form));
    }

    @PostMapping("/sys/monitor/script/delete")
    @Operation(summary = "删除脚本")
    @SaCheckPermission("sys:monitor:script:delete")
    public Result<String> delete(@Valid @RequestBody IdForm form) {
        scriptService.delete(form.getId());
        return Result.success("删除成功");
    }
}
