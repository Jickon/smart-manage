package sm.cloud.sys.monitor.script.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScriptListVO {
    private String id;
    private String number;
    private String name;
    private String remark;
    private LocalDateTime createTime;
}
