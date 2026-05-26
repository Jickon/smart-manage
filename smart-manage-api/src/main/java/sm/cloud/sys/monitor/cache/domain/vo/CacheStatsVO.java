package sm.cloud.sys.monitor.cache.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 缓存统计汇总
 *
 * @author Chekfu
 */
@Data
@Schema(description = "缓存统计汇总")
public class CacheStatsVO {
    @Schema(description = "Caffeine 本地缓存统计列表")
    private List<CaffeineCacheVO> caffeineCaches;

    @Schema(description = "Redis 服务器信息")
    private RedisInfoVO redisInfo;
}
