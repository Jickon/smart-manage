package sm.domain.sys.monitor.cache.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Redis key 信息
 *
 * @author Chekfu
 */
@Data
@Schema(description = "Redis key 信息")
public class RedisKeyVO {
    @Schema(description = "key 名称")
    private String key;

    @Schema(description = "key 类型（string/hash/list/set/zset）")
    private String type;

    @Schema(description = "剩余过期时间（秒），-1 表示永不过期，-2 表示不存在")
    private long ttl;
}
