package sm.cloud.sys.base.app.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 应用新增默认值
 */
@Data
@Schema(title = "应用新增默认值")
public class AppCreateNewDataVO {

	@Schema(description = "排序")
	private Integer seq;

	@Schema(description = "启用")
	private Boolean enableFlag;
}

