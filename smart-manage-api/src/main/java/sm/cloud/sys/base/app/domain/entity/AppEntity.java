package sm.cloud.sys.base.app.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 系统建模-应用（映射 t_sys_app）
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_app")
public class AppEntity extends BaseEntity {
	@Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
	private Long id;

	private String name;

	private String number;

	private String icon;

	private String iconColor;

	private Integer seq;

	private String description;

	private Long cloudId;

	private Boolean enableFlag;
}
