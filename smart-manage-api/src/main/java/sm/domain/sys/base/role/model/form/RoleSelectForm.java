package sm.domain.sys.base.role.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.form.PageForm;

/**
 * 角色-基础资料选择-分页查询表单。
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色-基础资料选择-分页查询")
public class RoleSelectForm extends PageForm {
	@Schema(description = "关键词（编码、名称模糊匹配）")
	private String keyword;
}
