package sm.cloud.sys.monitor.sql.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.form.PageForm;

/**
 * SQL 日志分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SQL 日志分页查询")
public class SqlLogListForm extends PageForm {

    @Schema(description = "关键字搜索（SQL 文本 LIKE）")
    private String keyword;

    @Schema(description = "结果类型过滤：QUERY/DML/DDL/ERROR")
    private String resultType;
}
