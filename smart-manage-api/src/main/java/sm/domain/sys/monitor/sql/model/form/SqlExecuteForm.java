package sm.domain.sys.monitor.sql.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * SQL 执行表单
 */
@Data
@Schema(description = "SQL 执行表单")
public class SqlExecuteForm {

    @NotBlank(message = "SQL 不能为空")
    @Schema(description = "要执行的 SQL 语句")
    private String sql;
}
