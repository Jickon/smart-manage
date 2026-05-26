package sm.cloud.sys.monitor.job.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 定时任务定义
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_job")
public class JobEntity extends BaseEntity {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /** 任务编码 */
    private String number;

    /** 任务名称 */
    private String jobName;

    /** 任务分组 */
    private String jobGroup;

    /** Job 实现类全限定名 */
    private String jobClassName;

    /** Cron 表达式 */
    private String cronExpression;

    /** 任务参数（JSON） */
    private String jobData;

    /** 状态：ENABLED / PAUSED */
    private String status;

    /** 是否系统内置 */
    private Boolean isSystem;

    /** 备注 */
    private String remark;
}
