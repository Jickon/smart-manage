package sm.cloud.sys.monitor.cache.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Redis key 查询表单（SCAN 游标分页）
 *
 * @author Chekfu
 */
@Data
@Schema(description = "Redis key 查询")
public class RedisKeysForm {
    @Schema(description = "SCAN 游标，首次传 \"0\"")
    private String cursor = "0";

    @Schema(description = "key 匹配模式，默认项目前缀*")
    private String pattern;

    @Schema(description = "每批次数量，默认 30")
    private Integer count = 30;
}
