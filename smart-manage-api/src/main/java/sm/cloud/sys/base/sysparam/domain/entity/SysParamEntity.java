package sm.cloud.sys.base.sysparam.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 系统参数实体
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_param")
public class SysParamEntity extends BaseEntity {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /** 参数编码 */
    private String number;

    /** 参数名称 */
    private String name;

    /** 参数值 */
    private String value;

    /** 备注 */
    private String remark;

    /** 是否系统内置 */
    private Boolean isSystem;
}
