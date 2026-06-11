package sm.cloud.sys.base.user.service;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.base.user.domain.entity.UserEntity;
import sm.cloud.sys.base.user.mapper.UserMapper;

import java.util.concurrent.TimeUnit;

/**
 * @author Chekfu
 */
@Service
@Slf4j
public class UserManage extends ServiceImpl<UserMapper, UserEntity> {

    /** Redis 远程缓存，确保重启不丢失 */
    @Cached(cacheType = CacheType.REMOTE, name = "userInfo", key = "#id", expire = 1, timeUnit = TimeUnit.HOURS)
    public UserEntity getById(Long id) {
        return baseMapper.selectById(id);
    }
}
