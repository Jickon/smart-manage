package sm.domain.sys.monitor.cache.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Redis 按前缀清除表单
 *
 * @author Chekfu
 */
@Data
@Schema(description = "Redis 按前缀清除")
public class RedisClearForm {
    @Schema(description = "key 前缀（不传则使用项目前缀）")
    private String prefix;
}
