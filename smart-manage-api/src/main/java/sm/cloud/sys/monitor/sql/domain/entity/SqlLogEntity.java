package sm.cloud.sys.monitor.sql.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * SQL 执行日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_sql_log")
public class SqlLogEntity extends BaseEntity {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    private String sqlText;
    private Integer executeDuration;
    private String resultType;
    private Integer rowCount;
    private String errorMessage;
    private String createName;
    private String createIp;
    private String remark;
}
