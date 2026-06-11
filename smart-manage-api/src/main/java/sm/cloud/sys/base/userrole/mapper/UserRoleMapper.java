package sm.cloud.sys.base.userrole.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sm.cloud.sys.base.userrole.domain.entity.UserRoleEntity;
import sm.cloud.sys.base.userrole.domain.vo.UserRoleVO;

import java.util.List;

/**
 * @author Chekfu
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
    List<UserRoleVO> selectByUserAndOrg(@Param("userId") Long userId, @Param("orgId") Long orgId);

    int insertBatch(@Param("entities") List<UserRoleEntity> entities);
}
