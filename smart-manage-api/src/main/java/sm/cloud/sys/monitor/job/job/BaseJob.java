package sm.cloud.sys.monitor.job.job;

import org.quartz.JobExecutionContext;

/**
 * 业务任务接口，所有定时任务实现此接口
 *
 * @author Chekfu
 */
public interface BaseJob {

    /**
     * 执行任务
     *
     * @param context Quartz 执行上下文
     */
    void execute(JobExecutionContext context) throws Exception;
}
