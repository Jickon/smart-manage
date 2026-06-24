package sm.domain.sys.base.cloud.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "云管理-保存")
public class CloudSaveForm {
	@Schema(description = "id，为空则新增")
	private Long id;

	@NotBlank(message = "名称不能为空")
	@Schema(description = "名称")
	private String name;

	@NotBlank(message = "编码不能为空")
	@Schema(description = "编码")
	private String number;

	@Schema(description = "排序")
	private Integer seq;

	@Schema(description = "启用")
	private Boolean enableFlag;
}

