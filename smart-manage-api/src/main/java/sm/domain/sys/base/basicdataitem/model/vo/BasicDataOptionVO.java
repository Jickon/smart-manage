package sm.domain.sys.base.basicdataitem.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础数据选项（供前端下拉框等组件消费）
 *
 * @author Chekfu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "基础数据选项")
public class BasicDataOptionVO {

    @Schema(description = "项编码")
    private String itemCode;

    @Schema(description = "项文本")
    private String itemLabel;
}
