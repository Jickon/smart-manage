package sm.domain.sys.monitor.cache.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.domain.sys.monitor.cache.model.form.CaffeineClearForm;
import sm.domain.sys.monitor.cache.model.form.RedisClearForm;
import sm.domain.sys.monitor.cache.model.form.RedisDeleteForm;
import sm.domain.sys.monitor.cache.model.form.RedisKeysForm;
import sm.domain.sys.monitor.cache.model.vo.CacheStatsVO;
import sm.domain.sys.monitor.cache.model.vo.RedisKeyVO;
import sm.domain.sys.monitor.cache.service.CacheService;
import sm.system.response.Result;

import java.util.List;

/**
 * 缓存管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "系统监控-缓存管理")
@RequiredArgsConstructor
public class CacheController {
    private final CacheService service;

    @Operation(summary = "缓存统计")
    @SaCheckPermission("sys:monitor:cache:listPage")
    @PostMapping("/sys/monitor/cache/stats")
    public Result<CacheStatsVO> stats() {
        return Result.success(service.getStats());
    }

    @Operation(summary = "清除 Caffeine 缓存")
    @SaCheckPermission("sys:monitor:cache:save")
    @PostMapping("/sys/monitor/cache/caffeine/clear")
    public Result<String> caffeineClear(@RequestBody CaffeineClearForm form) {
        service.clearCaffeine(form.getCacheName());
        return Result.success("清除成功");
    }

    @Operation(summary = "Redis key 列表")
    @SaCheckPermission("sys:monitor:cache:listPage")
    @PostMapping("/sys/monitor/cache/redis/keys")
    public Result<List<RedisKeyVO>> redisKeys(@RequestBody RedisKeysForm form) {
        return Result.success(service.getRedisKeys(form.getPattern()));
    }

    @Operation(summary = "删除 Redis key")
    @SaCheckPermission("sys:monitor:cache:save")
    @PostMapping("/sys/monitor/cache/redis/delete")
    public Result<Long> redisDelete(@Valid @RequestBody RedisDeleteForm form) {
        return Result.success(service.deleteRedisKeys(form.getKeys()));
    }

    @Operation(summary = "按前缀清除 Redis key")
    @SaCheckPermission("sys:monitor:cache:save")
    @PostMapping("/sys/monitor/cache/redis/clear")
    public Result<Long> redisClear(@RequestBody RedisClearForm form) {
        return Result.success(service.clearRedisByPrefix(form.getPrefix()));
    }
}
