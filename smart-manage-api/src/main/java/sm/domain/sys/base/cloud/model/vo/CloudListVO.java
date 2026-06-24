package sm.domain.sys.base.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "云管理-列表项")
public class CloudListVO {
	private Long id;
	private String name;
	private String number;
	private Integer seq;
	private Boolean enableFlag;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
}

