package sm.domain.sys.base.org.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.form.PageForm;

/**
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "组织列表表单")
public class OrgListForm extends PageForm {
}
