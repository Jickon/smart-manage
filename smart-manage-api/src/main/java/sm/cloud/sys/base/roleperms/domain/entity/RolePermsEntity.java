package sm.cloud.sys.base.roleperms.domain.entity;

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
@Table("t_sys_role_perms")
public class RolePermsEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;
	/*
	 * 角色ID
	 */
	private Long roleId;
	/*
	 * 权限ID
	 */
	private Long permissionId;
}
