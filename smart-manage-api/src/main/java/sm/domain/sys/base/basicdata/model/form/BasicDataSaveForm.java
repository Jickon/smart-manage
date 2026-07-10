package sm.domain.sys.base.basicdata.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    @Schema(description = "乐观锁版本号，修改时必传")
    private Integer mutex;

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

    @Valid
    @NotNull(message = "基础数据明细不能为空")
    @Schema(description = "基础数据明细")
    private List<BasicDataEntryForm> entrys;
}
