package sm.cloud.sys.monitor.job.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;
import sm.cloud.sys.monitor.job.domain.entity.JobEntity;
import sm.cloud.sys.monitor.job.domain.entity.JobLogEntity;
import sm.cloud.sys.monitor.job.domain.entity.table.JobTable;
import sm.cloud.sys.monitor.job.mapper.JobLogMapper;
import sm.cloud.sys.monitor.job.mapper.JobMapper;

import java.time.LocalDateTime;

/**
 * Quartz 全局 Job 监听器：记录每次执行到 t_sys_job_log
 *
 * @author Chekfu
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JobExecutionListener implements JobListener {

    private final JobLogMapper jobLogMapper;
    private final JobMapper jobMapper;

    @Override
    public String getName() {
        return "JobExecutionListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().getName();
        String jobGroup = context.getJobDetail().getKey().getGroup();

        JobEntity jobEntity = jobMapper.selectOneByQuery(
                QueryWrapper.create().from(JobTable.JOB)
                        .where(JobTable.JOB.JOB_NAME.eq(jobName))
                        .and(JobTable.JOB.JOB_GROUP.eq(jobGroup)));

        JobLogEntity logEntity = new JobLogEntity();
        logEntity.setJobId(jobEntity != null ? jobEntity.getId() : null);
        logEntity.setJobName(jobName);
        logEntity.setJobGroup(jobGroup);
        logEntity.setStartTime(LocalDateTime.now());
        logEntity.setStatus("RUNNING");
        logEntity.setCreateTime(LocalDateTime.now());
        jobLogMapper.insert(logEntity);

        context.put("__jobLogId__", logEntity.getId());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // 任务被否决，无需处理
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Long logId = (Long) context.get("__jobLogId__");
        if (logId == null) {
            return;
        }
        JobLogEntity logEntity = jobLogMapper.selectOneById(logId);
        if (logEntity == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        logEntity.setEndTime(now);
        logEntity.setDurationMs(java.time.Duration.between(logEntity.getStartTime(), now).toMillis());
        if (jobException != null) {
            logEntity.setStatus("FAILED");
            logEntity.setErrorMessage(truncate(jobException.getMessage(), 2000));
        } else {
            logEntity.setStatus("SUCCESS");
        }
        jobLogMapper.update(logEntity);
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return null;
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }
}
