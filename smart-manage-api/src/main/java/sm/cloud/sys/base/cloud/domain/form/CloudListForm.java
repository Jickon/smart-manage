package sm.cloud.sys.base.cloud.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.form.PageForm;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "云管理-分页查询")
public class CloudListForm extends PageForm {
	@Schema(description = "关键字（名称、编码模糊匹配）")
	private String keyword;

	@Schema(description = "启用状态")
	private Boolean enableFlag;
}
