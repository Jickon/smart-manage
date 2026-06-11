package sm.cloud.sys.base.attachment.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 附件实体
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_attachment")
public class AttachmentEntity extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String originalName;
    private String storedName;
    private String storedPath;
    private Long fileSize;
    private String mimeType;
    private String fileExt;
    private String storageType;
    private Boolean isTemp;
}
