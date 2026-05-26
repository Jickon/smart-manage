package sm.cloud.sys.base.menu.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.cloud.sys.common.enums.MenuLevelEnum;
import sm.system.entity.BaseEntity;

/**
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_menu")
public class MenuEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;
	/**
	 * 编号
	 */
	private String number;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 类型
	 */
	private MenuLevelEnum level;
	/**
	 * 父菜单ID，一级菜单为0
	 */
	private Long parentId;
	/**
	 * 应用ID
	 */
	private Long appId;
	/**
	 * 权限ID
	 */
	private Long permissionId;
	/**
	 * 路径
	 */
	private String path;
	/**
	 * 组件路径
	 */
	private String component;
	/**
	 * 图标
	 */
	private String icon;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 是否启用
	 */
	private Boolean enableFlag;
}
