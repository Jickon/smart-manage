package sm.cloud.sys.base.basicdata.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_sys_basic_data")
public class BasicDataEntity extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
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
