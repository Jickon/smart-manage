package sm.domain.sys.base.fileconfig.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.form.PageForm;

/**
 * 文件配置列表查询表单
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件配置列表表单")
public class FileConfigListForm extends PageForm {

    @Schema(description = "关键词")
    private String keyword;
}
