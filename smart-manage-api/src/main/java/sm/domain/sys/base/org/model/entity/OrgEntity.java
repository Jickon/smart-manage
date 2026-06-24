package sm.domain.sys.base.org.model.entity;

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
@TableName("t_sys_org")
public class OrgEntity extends BaseEntity {
	@TableId(type = IdType.ASSIGN_ID)
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
