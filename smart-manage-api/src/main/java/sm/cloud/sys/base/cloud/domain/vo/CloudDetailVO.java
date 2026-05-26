package sm.cloud.sys.base.cloud.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 云详情 VO
 *
 * @author Chekfu
 */
@Data
@Schema(description = "云详情")
public class CloudDetailVO {

	@Schema(description = "ID")
	private String id;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "编码")
	private String number;

	@Schema(description = "排序")
	private Integer seq;

	@Schema(description = "是否启用")
	private Boolean enableFlag;

	@Schema(description = "创建时间")
	private LocalDateTime createTime;

	@Schema(description = "更新时间")
	private LocalDateTime updateTime;

	@Schema(description = "创建人")
	private Long createUser;

	@Schema(description = "修改人")
	private Long updateUser;
}
