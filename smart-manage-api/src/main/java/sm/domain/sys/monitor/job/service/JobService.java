package sm.domain.sys.monitor.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import sm.domain.sys.monitor.job.model.entity.JobEntity;
import sm.domain.sys.monitor.job.model.entity.JobLogEntity;
import sm.domain.sys.monitor.job.model.form.JobListForm;
import sm.domain.sys.monitor.job.model.form.JobSaveForm;
import sm.domain.sys.monitor.job.model.vo.JobDetailVO;
import sm.domain.sys.monitor.job.model.vo.JobListVO;
import sm.domain.sys.monitor.job.mapper.JobLogMapper;
import sm.domain.sys.monitor.job.mapper.JobMapper;
import sm.system.exception.BizException;
import sm.system.response.PageData;
import sm.system.response.ResultEnum;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定时任务管理 Service
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

    private final JobMapper mapper;
    private final JobLogMapper jobLogMapper;
    private final Scheduler scheduler;
    private final ApplicationContext applicationContext;
    private final JobTxService txService;

    // ==================== 查询 ====================

    public PageData<JobListVO> listPage(JobListForm form) {
        LambdaQueryWrapper<JobEntity> qw = new LambdaQueryWrapper<JobEntity>();
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.and(condition -> condition.like(JobEntity::getJobName, kw).or().like(JobEntity::getJobGroup, kw).or().like(JobEntity::getNumber, kw));
        }
        if (form.getStatus() != null && !form.getStatus().isBlank()) {
            qw.eq(JobEntity::getStatus, form.getStatus());
        }
        qw.orderByDesc(JobEntity::getCreateTime);

        Page<JobEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
        Page<JobEntity> result = mapper.selectPage(page, qw);
        List<JobListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
        return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), vos);
    }

    public JobDetailVO getById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "任务ID不能为空");
        }
        JobEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "任务不存在");
        }
        return toDetailVo(entity);
    }

    // ==================== 增删改 ====================

    public Long save(JobSaveForm form) {
        return txService.save(form);
    }

    public void deleteById(Long id) {
        txService.deleteById(id);
    }

    // ==================== 任务操作 ====================

    public void pause(Long id) {
        JobEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException("任务不存在");
        }
        try {
            JobKey jobKey = JobKey.jobKey(entity.getJobName(), entity.getJobGroup());
            scheduler.pauseJob(jobKey);
            entity.setStatus("PAUSED");
            mapper.updateById(entity);
        } catch (SchedulerException e) {
            throw new BizException("暂停任务失败: " + e.getMessage());
        }
    }

    public void resume(Long id) {
        JobEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException("任务不存在");
        }
        try {
            JobKey jobKey = JobKey.jobKey(entity.getJobName(), entity.getJobGroup());
            scheduler.resumeJob(jobKey);
            entity.setStatus("ENABLED");
            mapper.updateById(entity);
        } catch (SchedulerException e) {
            throw new BizException("恢复任务失败: " + e.getMessage());
        }
    }

    public void trigger(Long id) {
        JobEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException("任务不存在");
        }
        try {
            JobKey jobKey = JobKey.jobKey(entity.getJobName(), entity.getJobGroup());
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            throw new BizException("触发任务失败: " + e.getMessage());
        }
    }

    // ==================== 可选 Job 类列表 ====================

    /**
     * 获取所有可用的 Job 实现类（Spring 容器中所有 Job 类型的 Bean）
     */
    public List<Map<String, String>> getAvailableJobClasses() {
        Map<String, Job> beans = applicationContext.getBeansOfType(Job.class);
        return beans.values().stream()
                .map(job -> {
                    Map<String, String> item = new LinkedHashMap<>();
                    item.put("className", job.getClass().getName());
                    item.put("simpleName", job.getClass().getSimpleName());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 新建任务时的默认值
     */
    public Map<String, Object> createNewData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("jobGroup", "DEFAULT");
        data.put("status", "ENABLED");
        data.put("cronExpression", "0 0 3 * * ?");
        return data;
    }

    // ==================== 内部方法 ====================

    /**
     * 获取某个任务的最后一次执行日志
     */
    private JobLogEntity getLastLog(Long jobId) {
        LambdaQueryWrapper<JobLogEntity> qw = new LambdaQueryWrapper<JobLogEntity>()
                .eq(JobLogEntity::getJobId, jobId)
                .orderByDesc(JobLogEntity::getCreateTime);
        Page<JobLogEntity> page = jobLogMapper.selectPage(new Page<>(1, 1, false), qw);
        return page.getRecords().isEmpty() ? null : page.getRecords().get(0);
    }

    private JobListVO toListVo(JobEntity entity) {
        JobListVO vo = new JobListVO();
        vo.setId(entity.getId());
        vo.setNumber(entity.getNumber());
        vo.setJobName(entity.getJobName());
        vo.setJobGroup(entity.getJobGroup());
        vo.setJobClassName(entity.getJobClassName());
        vo.setCronExpression(entity.getCronExpression());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setIsSystem(entity.getIsSystem());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        JobLogEntity lastLog = getLastLog(entity.getId());
        if (lastLog != null) {
            vo.setLastExecuteTime(lastLog.getStartTime());
            vo.setLastExecuteStatus(lastLog.getStatus());
        }
        return vo;
    }

    private JobDetailVO toDetailVo(JobEntity entity) {
        JobDetailVO vo = new JobDetailVO();
        vo.setId(entity.getId());
        vo.setNumber(entity.getNumber());
        vo.setJobName(entity.getJobName());
        vo.setJobGroup(entity.getJobGroup());
        vo.setJobClassName(entity.getJobClassName());
        vo.setCronExpression(entity.getCronExpression());
        vo.setJobData(entity.getJobData());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setIsSystem(entity.getIsSystem());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        JobLogEntity lastLog = getLastLog(entity.getId());
        if (lastLog != null) {
            vo.setLastExecuteTime(lastLog.getStartTime());
            vo.setLastExecuteStatus(lastLog.getStatus());
        }
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(entity.getJobName() + "_trigger", entity.getJobGroup());
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger != null && trigger.getNextFireTime() != null) {
                vo.setNextFireTime(trigger.getNextFireTime().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            }
        } catch (SchedulerException e) {
            log.debug("获取下次触发时间失败: {}", entity.getJobName(), e);
        }
        return vo;
    }
}
