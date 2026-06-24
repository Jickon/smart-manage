package sm.domain.sys.base.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "云-基础资料选择-列表项")
public class CloudSelectVO {
	private Long id;
	private String name;
	private String number;
	private Boolean enableFlag;
}

