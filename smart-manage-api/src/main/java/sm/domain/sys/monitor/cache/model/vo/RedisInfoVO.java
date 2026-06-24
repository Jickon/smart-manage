package sm.domain.sys.monitor.cache.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Redis 服务器信息
 *
 * @author Chekfu
 */
@Data
@Schema(description = "Redis 服务器信息")
public class RedisInfoVO {
    @Schema(description = "Redis 版本")
    private String version;

    @Schema(description = "运行天数")
    private long uptimeDays;

    @Schema(description = "已用内存（人类可读）")
    private String usedMemoryHuman;

    @Schema(description = "连接客户端数")
    private int connectedClients;

    @Schema(description = "当前 DB key 数量")
    private long dbSize;

    @Schema(description = "当前 DB 索引")
    private int dbIndex;

    @Schema(description = "项目前缀")
    private String keyPrefix;
}
