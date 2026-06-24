package sm.domain.sys.base.userrole.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_user_role")
public class UserRoleEntity extends BaseEntity {
	@TableId(type = IdType.ASSIGN_ID)
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
