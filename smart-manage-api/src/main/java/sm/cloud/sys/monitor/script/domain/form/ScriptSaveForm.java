package sm.cloud.sys.monitor.script.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "脚本保存表单")
public class ScriptSaveForm {
    @Schema(description = "主键ID（新建时不传）")
    private Long id;

    @NotBlank(message = "编码不能为空")
    @Schema(description = "编码")
    private String number;

    @NotBlank(message = "名称不能为空")
    @Schema(description = "名称")
    private String name;

    @NotBlank(message = "脚本内容不能为空")
    @Schema(description = "脚本内容")
    private String content;

    @Schema(description = "备注")
    private String remark;
}
