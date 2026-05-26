package sm.cloud.sys.monitor.job.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务执行实例（即执行日志）
 *
 * @author Chekfu
 */
@Data
@Table("t_sys_job_log")
public class JobLogEntity {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /** 关联的任务ID */
    private Long jobId;

    /** 任务名称 */
    private String jobName;

    /** 任务分组 */
    private String jobGroup;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 耗时（毫秒） */
    private Long durationMs;

    /** 状态：RUNNING / SUCCESS / FAILED */
    private String status;

    /** 错误信息 */
    private String errorMessage;

    /** 创建时间 */
    private LocalDateTime createTime;
}
