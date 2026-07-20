package sm.domain.sys.base.basicdata.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 基础数据明细实体，生命周期完全从属于 BasicData。
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_basic_data_entry")
public class BasicDataEntryEntity extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 父级基础数据ID */
    private Long parentId;

    /** 编码，在同一父级下唯一 */
    private String number;

    /** 名称 */
    private String name;

    /** 排序 */
    private Integer sort;

    /** 是否启用 */
    private Boolean enabled;
}
