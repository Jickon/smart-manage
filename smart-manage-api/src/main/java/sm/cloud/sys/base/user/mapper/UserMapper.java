package sm.cloud.sys.base.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.cloud.sys.base.user.domain.entity.UserEntity;

/**
 * @author Chekfu
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}