package sm.domain.sys.base.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sm.domain.sys.base.role.model.entity.RoleEntity;

/**
 * @author Chekfu
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    java.util.List<String> selectUserRoleNumbers(
            @Param("userId") Long userId,
            @Param("orgId") Long orgId);
}
