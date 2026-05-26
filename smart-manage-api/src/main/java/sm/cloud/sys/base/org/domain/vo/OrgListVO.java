package sm.cloud.sys.base.org.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chekfu
 */
@Data
@Schema(title = "组织列表视图")
public class OrgListVO {
	@Schema(description = "id")
	private Long id;

	@Schema(description = "组织名称")
	private String name;

	@Schema(description = "组织编号")
	private String number;

	@Schema(description = "上级组织id")
	private Long parentId;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "备注")
	private String remark;
}
