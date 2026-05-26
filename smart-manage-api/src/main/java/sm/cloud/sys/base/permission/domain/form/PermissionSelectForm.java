package sm.cloud.sys.base.permission.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.form.PageForm;

/**
 * 权限-基础资料选择-分页查询表单。
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "权限-基础资料选择-分页查询")
public class PermissionSelectForm extends PageForm {
	@Schema(description = "关键词（编码、名称模糊匹配）")
	private String keyword;

	@Schema(description = "应用ID（必填，按应用过滤）")
	private Long appId;
}
