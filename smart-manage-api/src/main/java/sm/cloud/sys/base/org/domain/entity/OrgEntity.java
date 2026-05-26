package sm.cloud.sys.base.org.domain.entity;

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
@Table("t_sys_org")
public class OrgEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;
	/*
	 * 名称
	 */
	private String name;
	/*
	 * 编码
	 */
	private String number;
	/*
	 * 上级组织ID，顶级组织为0
	 */
	private Long parentId;
	/*
	 * 排序
	 */
	private Integer sort;
}
