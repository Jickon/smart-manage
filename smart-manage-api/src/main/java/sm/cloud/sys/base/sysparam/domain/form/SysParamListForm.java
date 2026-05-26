package sm.cloud.sys.base.sysparam.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.form.PageForm;

/**
 * 系统参数列表查询表单
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统参数列表表单")
public class SysParamListForm extends PageForm {

    @Schema(description = "关键词")
    private String keyword;
}
