package sm.cloud.sys.monitor.operatelog.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 操作日志（BizLog）
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_operate_log")
public class OperateLogEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;
	private String bizName;
	private Boolean success;
	private String errorMsg;
	private String requestMethod;
	private String requestUri;
	private String ip;
	private String userAgent;
	private String className;
	private String methodName;
	private Long durationMs;
	private String requestParams;
	private String responseBody;
	private Long userId;
	private String username;
}
