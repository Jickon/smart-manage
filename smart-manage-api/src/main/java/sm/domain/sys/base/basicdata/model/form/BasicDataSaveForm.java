package sm.domain.sys.base.basicdata.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import sm.domain.sys.base.basicdataitem.model.form.BasicDataItemSaveForm;

import java.util.List;

/**
 * 基础数据保存表单（含明细，entrys 为通用明细字段名）
 *
 * @author Chekfu
 */
@Data
@Schema(description = "基础数据保存表单")
public class BasicDataSaveForm {

    @Schema(description = "ID（新增时不传）")
    private Long id;

    @NotBlank(message = "编码不能为空")
    @Schema(description = "编码")
    private String number;

    @NotBlank(message = "名称不能为空")
    @Schema(description = "名称")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "基础数据项明细")
    private List<BasicDataItemSaveForm> entrys;
}
