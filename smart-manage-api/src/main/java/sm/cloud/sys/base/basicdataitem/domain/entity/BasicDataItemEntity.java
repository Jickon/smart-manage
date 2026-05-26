package sm.cloud.sys.base.basicdataitem.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 基础数据项实体
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_basic_data_item")
public class BasicDataItemEntity extends BaseEntity {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /** 基础数据编码 */
    private String typeNumber;

    /** 项编码 */
    private String itemCode;

    /** 项文本 */
    private String itemLabel;

    /** 排序 */
    private Integer sort;

    /** 是否启用 */
    private Boolean enableFlag;
}
