package sm.cloud.sys.base.basicdata.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 基础数据实体
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_basic_data")
public class BasicDataEntity extends BaseEntity {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /** 编码，唯一 */
    private String number;

    /** 名称 */
    private String name;

    /** 备注 */
    private String remark;

    /** 是否启用 */
    private Boolean enableFlag;
}
