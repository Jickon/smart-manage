package sm.domain.sys.monitor.cache.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Caffeine 缓存清除表单
 *
 * @author Chekfu
 */
@Data
@Schema(description = "Caffeine 缓存清除")
public class CaffeineClearForm {
    @Schema(description = "缓存名（basic-data-options/sys-params/common），不传则清空全部")
    private String cacheName;
}
