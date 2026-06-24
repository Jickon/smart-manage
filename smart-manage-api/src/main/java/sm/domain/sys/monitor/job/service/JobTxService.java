package sm.domain.sys.monitor.job.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.monitor.job.model.entity.JobEntity;
import sm.domain.sys.monitor.job.model.entity.JobLogEntity;
import sm.domain.sys.monitor.job.model.form.JobSaveForm;
import sm.domain.sys.monitor.job.mapper.JobLogMapper;
import sm.domain.sys.monitor.job.mapper.JobMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.util.Map;

/**
 * 定时任务事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class JobTxService {

    private final JobMapper mapper;
    private final JobLogMapper jobLogMapper;
    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;

    public Long save(JobSaveForm form) {
        JobEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException("任务不存在");
            }
            // 系统内置任务不允许修改 jobClassName 和 jobGroup
            if (Boolean.TRUE.equals(entity.getIsSystem())) {
                if (!entity.getJobClassName().equals(form.getJobClassName())) {
                    throw new BizException("系统内置任务不可修改执行类");
                }
                if (!entity.getJobGroup().equals(form.getJobGroup())) {
                    throw new BizException("系统内置任务不可修改任务分组");
                }
            }
            if (!entity.getJobName().equals(form.getJobName()) || !entity.getJobGroup().equals(form.getJobGroup())) {
                removeQuartzJob(entity.getJobName(), entity.getJobGroup());
            }
        } else {
            entity = new JobEntity();
        }

        // number 仅在新增时设置（编辑不可改）
        if (form.getId() == null) {
            entity.setNumber(form.getNumber());
        }
        entity.setJobName(form.getJobName());
        entity.setJobGroup(form.getJobGroup() != null ? form.getJobGroup() : "DEFAULT");
        entity.setJobClassName(form.getJobClassName());
        entity.setCronExpression(form.getCronExpression());
        entity.setJobData(form.getJobData());
        entity.setStatus(form.getStatus() != null ? form.getStatus() : "ENABLED");
        entity.setRemark(form.getRemark());

        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }

        // 解析 JobClass，SpringBeanJobFactory 负责依赖注入
        Class<? extends Job> jobClass = resolveJobClass(entity.getJobClassName());

        // 解析 jobData JSON 为独立的 JobDataMap 键值对
        JobDataMap dataMap = parseJobData(entity.getJobData());

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(entity.getJobName(), entity.getJobGroup())
                .withDescription(entity.getRemark())
                .usingJobData(dataMap)
                .storeDurably()
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(entity.getJobName() + "_trigger", entity.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(entity.getCronExpression())
                        .withMisfireHandlingInstructionDoNothing())
                .build();

        try {
            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.addJob(jobDetail, true);
                scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else {
                scheduler.scheduleJob(jobDetail, trigger);
            }
            if ("PAUSED".equals(entity.getStatus())) {
                scheduler.pauseJob(jobDetail.getKey());
            }
        } catch (SchedulerException e) {
            log.error("Quartz 调度失败", e);
            throw new BizException("任务调度失败: " + e.getMessage());
        }

        return entity.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "任务ID不能为空");
        }
        JobEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "任务不存在");
        }
        if (Boolean.TRUE.equals(entity.getIsSystem())) {
            throw new BizException("系统内置任务不可删除");
        }
        removeQuartzJob(entity.getJobName(), entity.getJobGroup());
        mapper.deleteById(id);
        // 删除关联的执行日志
        jobLogMapper.delete(new LambdaQueryWrapper<JobLogEntity>()
                .eq(JobLogEntity::getJobId, id));
    }

    // ==================== 内部方法 ====================

    /**
     * 解析任务类名，确保其实现了 org.quartz.Job 接口
     */
    private Class<? extends Job> resolveJobClass(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!Job.class.isAssignableFrom(clazz)) {
                throw new BizException("任务类必须实现 org.quartz.Job 接口: " + className);
            }
            return clazz.asSubclass(Job.class);
        } catch (ClassNotFoundException e) {
            throw new BizException("找不到任务类: " + className);
        }
    }

    /**
     * 解析 jobData JSON 字符串为 Quartz JobDataMap
     */
    @SuppressWarnings("unchecked")
    private JobDataMap parseJobData(String jobDataJson) {
        JobDataMap dataMap = new JobDataMap();
        if (jobDataJson != null && !jobDataJson.isBlank()) {
            try {
                Map<String, Object> map = objectMapper.readValue(jobDataJson, new TypeReference<>() {});
                map.forEach(dataMap::put);
            } catch (Exception e) {
                log.warn("jobData JSON 解析失败，将忽略自定义参数: {}", jobDataJson, e);
            }
        }
        return dataMap;
    }

    private void removeQuartzJob(String jobName, String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName + "_trigger", jobGroup);
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.warn("删除 Quartz Job 失败: {}/{}", jobGroup, jobName, e);
        }
    }
}
