package sm.cloud.sys.monitor.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.cloud.sys.monitor.job.domain.form.JobLogListForm;
import sm.cloud.sys.monitor.job.domain.vo.JobLogListVO;
import sm.cloud.sys.monitor.job.service.JobLogService;
import sm.system.response.PageResult;
import sm.system.response.Result;

import java.util.List;

/**
 * 执行实例/执行日志
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "系统监控-执行实例", description = "执行实例查询接口")
@RequiredArgsConstructor
public class JobLogController {

    private final JobLogService service;

    @PostMapping("/sys/monitor/job/log/listPage")
    @Operation(summary = "执行实例列表", description = "获取任务执行实例分页列表，支持按状态筛选")
    @SaCheckPermission("sys:monitor:job-log:listPage")
    public Result<PageResult<JobLogListVO>> listPage(@RequestBody JobLogListForm form) {
        return Result.success(service.listPage(form));
    }

    @PostMapping("/sys/monitor/job/log/running")
    @Operation(summary = "正在运行的实例", description = "查询当前状态为 RUNNING 的执行实例")
    @SaCheckPermission("sys:monitor:job-log:listPage")
    public Result<List<JobLogListVO>> running() {
        return Result.success(service.running());
    }
}
