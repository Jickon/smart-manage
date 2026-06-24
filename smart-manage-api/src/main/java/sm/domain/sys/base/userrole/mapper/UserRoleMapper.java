package sm.domain.sys.base.userrole.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sm.domain.sys.base.userrole.model.entity.UserRoleEntity;
import sm.domain.sys.base.userrole.model.vo.UserRoleVO;

import java.util.List;

/**
 * @author Chekfu
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
    List<UserRoleVO> selectByUserAndOrg(@Param("userId") Long userId, @Param("orgId") Long orgId);

    int insertBatch(@Param("entities") List<UserRoleEntity> entities);
}
