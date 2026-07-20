package sm.domain.sys.base.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 云新增默认值
 */
@Data
@Schema(title = "云新增默认值")
public class CloudCreateNewDataVO {

	@Schema(description = "排序")
	private Integer seq;

	@Schema(description = "启用")
	private Boolean enabled;
}

