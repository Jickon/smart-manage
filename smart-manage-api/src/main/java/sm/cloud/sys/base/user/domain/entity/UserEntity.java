package sm.cloud.sys.base.user.domain.entity;

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
@Table("t_sys_user")
public class UserEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;
	/*
	 * 用户名
	 */
	private String username;
	/*
	 * 密码
	 */
	private String password;
	/*
	 * 昵称
	 */
	private String nickname;
	/*
	 * 头像地址
	 */
	private String avatar;
	/*
	 * 邮箱地址
	 */
	private String email;
	/*
	 * 手机号
	 */
	private String phone;
	/*
	 * 主题颜色
	 */
	private String themeColor;
	/*
	 * 是否可用
	 */
	private Boolean enableFlag;

}