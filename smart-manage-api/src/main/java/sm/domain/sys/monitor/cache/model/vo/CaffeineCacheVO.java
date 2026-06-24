package sm.domain.sys.monitor.cache.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 单个 Caffeine 缓存统计信息
 *
 * @author Chekfu
 */
@Data
@Schema(description = "Caffeine 缓存统计")
public class CaffeineCacheVO {
    @Schema(description = "缓存名")
    private String name;

    @Schema(description = "命中次数")
    private long hitCount;

    @Schema(description = "未命中次数")
    private long missCount;

    @Schema(description = "命中率")
    private double hitRate;

    @Schema(description = "驱逐次数")
    private long evictionCount;

    @Schema(description = "当前估计条目数")
    private long estimatedSize;

    @Schema(description = "总请求次数")
    private long requestCount;
}
