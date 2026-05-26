package sm.cloud.sys.base.cloud.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 系统建模-云（映射 t_sys_cloud）
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_cloud")
public class CloudEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;

	private String name;

	private String number;

	private Integer seq;

	private Boolean enableFlag;
}
