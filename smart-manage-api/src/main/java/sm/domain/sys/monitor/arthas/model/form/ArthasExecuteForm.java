package sm.domain.sys.monitor.arthas.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Arthas 命令执行表单
 */
@Data
@Schema(description = "Arthas 命令执行")
public class ArthasExecuteForm {

    @NotBlank(message = "命令不能为空")
    @Schema(description = "命令名称：thread, trace, watch, stack, tt, logger, vmoption, dashboard, jvm", requiredMode = Schema.RequiredMode.REQUIRED)
    private String command;

    @Schema(description = "命令参数：如 \"-n 5\" 或 \"com.example.MyService myMethod\"", defaultValue = "")
    private String args;
}
