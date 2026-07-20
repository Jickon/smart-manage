package sm.domain.sys.base.basicdata.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 基础数据明细表单。
 *
 * @author Chekfu
 */
@Data
@Schema(description = "基础数据明细表单")
public class BasicDataEntryForm {

    @NotBlank(message = "明细编码不能为空")
    @Schema(description = "编码")
    private String number;

    @NotBlank(message = "明细名称不能为空")
    @Schema(description = "名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;

}
