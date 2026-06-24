package sm.domain.sys.monitor.script.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "脚本执行结果")
public class ScriptResultVO {
    @Schema(description = "执行输出")
    private String output;

    @Schema(description = "执行耗时（毫秒）")
    private Integer executeDuration;
}
