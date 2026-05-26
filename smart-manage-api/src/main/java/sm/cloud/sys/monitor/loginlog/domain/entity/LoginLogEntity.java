package sm.cloud.sys.monitor.loginlog.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 登录/登出日志
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_login_log")
public class LoginLogEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;
	private Long userId;
	private String username;
	private String nickname;
	private String eventType;
	private Boolean success;
	private String failReason;
	private String ip;
	private String userAgent;
	private String tokenHint;
}
