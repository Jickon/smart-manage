package sm.domain.sys.monitor.sql.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * SQL 执行结果
 */
@Data
@Schema(description = "SQL 执行结果")
public class SqlResultVO {

    @Schema(description = "结果类型: QUERY / DML / DDL / ERROR")
    private String type;

    @Schema(description = "列名列表（仅 QUERY 类型）")
    private List<String> columns;

    @Schema(description = "列注释列表（仅 QUERY 类型，与 columns 一一对应）")
    private List<String> comments;

    @Schema(description = "数据行（仅 QUERY 类型）")
    private List<Map<String, Object>> rows;

    @Schema(description = "影响/返回行数")
    private Integer rowCount;

    @Schema(description = "执行耗时（ms）")
    private Integer executeDuration;

    @Schema(description = "提示/错误消息")
    private String message;
}
