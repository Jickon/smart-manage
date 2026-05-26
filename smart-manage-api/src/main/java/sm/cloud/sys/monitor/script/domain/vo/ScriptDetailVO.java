package sm.cloud.sys.monitor.script.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptDetailVO extends ScriptListVO {
    private String content;
    private LocalDateTime updateTime;
}
