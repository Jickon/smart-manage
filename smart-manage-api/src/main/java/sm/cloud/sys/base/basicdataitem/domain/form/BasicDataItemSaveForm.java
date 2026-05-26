package sm.cloud.sys.base.basicdataitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 基础数据项保存表单
 *
 * @author Chekfu
 */
@Data
@Schema(description = "基础数据项保存表单")
public class BasicDataItemSaveForm {

    @Schema(description = "ID（新增时不传）")
    private Long id;

    @Schema(description = "基础数据编码（由父级 save 自动填充）")
    private String typeNumber;

    @NotBlank(message = "项编码不能为空")
    @Schema(description = "项编码")
    private String itemCode;

    @NotBlank(message = "项文本不能为空")
    @Schema(description = "项文本")
    private String itemLabel;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否启用")
    private Boolean enableFlag;
}
