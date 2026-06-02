package sm.cloud.sys.monitor.job.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.monitor.job.domain.entity.JobEntity;
import sm.cloud.sys.monitor.job.domain.entity.JobLogEntity;
import sm.cloud.sys.monitor.job.domain.entity.table.JobLogTable;
import sm.cloud.sys.monitor.job.domain.entity.table.JobTable;
import sm.cloud.sys.monitor.job.domain.form.JobListForm;
import sm.cloud.sys.monitor.job.domain.form.JobSaveForm;
import sm.cloud.sys.monitor.job.domain.vo.JobDetailVO;
import sm.cloud.sys.monitor.job.domain.vo.JobListVO;
import sm.cloud.sys.monitor.job.mapper.JobLogMapper;
import sm.cloud.sys.monitor.job.mapper.JobMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;
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
    private final ObjectMapper objectMapper;

    // ==================== 查询 ====================

    public PageResult<JobListVO> listPage(JobListForm form) {
        QueryWrapper qw = QueryWrapper.create().from(JobTable.JOB);
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.and(JobTable.JOB.JOB_NAME.like(kw)
                    .or(JobTable.JOB.JOB_GROUP.like(kw))
                    .or(JobTable.JOB.NUMBER.like(kw)));
        }
        if (form.getStatus() != null && !form.getStatus().isBlank()) {
            qw.and(JobTable.JOB.STATUS.eq(form.getStatus()));
        }
        qw.orderBy(JobTable.JOB.CREATE_TIME, false);

        Page<JobEntity> page = Page.of(form.getPageNum(), form.getPageSize());
        Page<JobEntity> result = mapper.paginate(page, qw);
        List<JobListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
        return PageResult.of(result.getTotalRow(), vos);
    }

    public JobDetailVO getById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "任务ID不能为空");
        }
        JobEntity entity = mapper.selectOneById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "任务不存在");
        }
        return toDetailVo(entity);
    }

    // ==================== 增删改 ====================

    @Transactional(rollbackFor = Exception.class)
    public Long save(JobSaveForm form) {
        JobEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectOneById(form.getId());
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
            mapper.update(entity);
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

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "任务ID不能为空");
        }
        JobEntity entity = mapper.selectOneById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "任务不存在");
        }
        if (Boolean.TRUE.equals(entity.getIsSystem())) {
            throw new BizException("系统内置任务不可删除");
        }
        removeQuartzJob(entity.getJobName(), entity.getJobGroup());
        mapper.deleteById(id);
        // 删除关联的执行日志
        QueryWrapper qw = QueryWrapper.create().from(JobLogTable.JOB_LOG)
                .where(JobLogTable.JOB_LOG.JOB_ID.eq(id));
        jobLogMapper.deleteByQuery(qw);
    }

    // ==================== 任务操作 ====================

    public void pause(Long id) {
        JobEntity entity = mapper.selectOneById(id);
        if (entity == null) {
            throw new BizException("任务不存在");
        }
        try {
            JobKey jobKey = JobKey.jobKey(entity.getJobName(), entity.getJobGroup());
            scheduler.pauseJob(jobKey);
            entity.setStatus("PAUSED");
            mapper.update(entity);
        } catch (SchedulerException e) {
            throw new BizException("暂停任务失败: " + e.getMessage());
        }
    }

    public void resume(Long id) {
        JobEntity entity = mapper.selectOneById(id);
        if (entity == null) {
            throw new BizException("任务不存在");
        }
        try {
            JobKey jobKey = JobKey.jobKey(entity.getJobName(), entity.getJobGroup());
            scheduler.resumeJob(jobKey);
            entity.setStatus("ENABLED");
            mapper.update(entity);
        } catch (SchedulerException e) {
            throw new BizException("恢复任务失败: " + e.getMessage());
        }
    }

    public void trigger(Long id) {
        JobEntity entity = mapper.selectOneById(id);
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

    /**
     * 获取某个任务的最后一次执行日志
     */
    private JobLogEntity getLastLog(Long jobId) {
        QueryWrapper qw = QueryWrapper.create().from(JobLogTable.JOB_LOG)
                .where(JobLogTable.JOB_LOG.JOB_ID.eq(jobId))
                .orderBy(JobLogTable.JOB_LOG.CREATE_TIME, false)
                .limit(1);
        return jobLogMapper.selectOneByQuery(qw);
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
