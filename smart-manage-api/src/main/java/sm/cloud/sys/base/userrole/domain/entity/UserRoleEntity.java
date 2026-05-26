package sm.cloud.sys.base.userrole.domain.entity;

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
@Table("t_sys_user_role")
public class UserRoleEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;
	/*
	 * 用户ID
	 */
	private Long userId;
	/*
	 * 组织ID
	 */
	private Long orgId;
	/*
	 * 角色ID
	 */
	private Long roleId;
}
