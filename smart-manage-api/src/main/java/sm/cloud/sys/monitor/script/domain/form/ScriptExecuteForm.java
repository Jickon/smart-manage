package sm.cloud.sys.monitor.script.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "脚本执行表单")
public class ScriptExecuteForm {
    @NotBlank(message = "脚本内容不能为空")
    @Schema(description = "脚本内容（JavaScript）")
    private String content;
}
