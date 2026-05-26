package sm.cloud.sys.base.permission.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_permission")
public class PermissionEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 编码
	 */
	private String number;
	/**
	 * 应用ID
	 */
	private Long appId;
}
