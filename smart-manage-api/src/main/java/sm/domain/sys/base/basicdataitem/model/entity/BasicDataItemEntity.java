package sm.domain.sys.base.basicdataitem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_sys_basic_data_item")
public class BasicDataItemEntity extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
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
