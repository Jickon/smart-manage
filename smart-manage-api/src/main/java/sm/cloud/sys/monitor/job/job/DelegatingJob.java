package sm.cloud.sys.monitor.job.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Quartz Job 桥接器：从 JobDataMap 中取 jobClass，从 Spring 容器获取对应的 BaseJob Bean 并委派执行
 *
 * @author Chekfu
 */
@Component
@Slf4j
public class DelegatingJob extends QuartzJobBean {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String jobClassName = dataMap.getString("jobClass");
        if (jobClassName == null || jobClassName.isBlank()) {
            log.error("JobDataMap 中未指定 jobClass，无法执行");
            return;
        }
        BaseJob job;
        try {
            Class<?> clazz = Class.forName(jobClassName);
            job = (BaseJob) applicationContext.getBean(clazz);
        } catch (Exception e) {
            log.error("无法获取 Job Bean: {}", jobClassName, e);
            throw new RuntimeException("无法获取 Job Bean: " + jobClassName, e);
        }
        try {
            job.execute(context);
        } catch (Exception e) {
            log.error("任务执行失败: {}", jobClassName, e);
            throw new RuntimeException("任务执行失败: " + jobClassName, e);
        }
    }
}
