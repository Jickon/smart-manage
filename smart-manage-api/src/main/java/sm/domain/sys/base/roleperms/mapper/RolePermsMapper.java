package sm.domain.sys.base.roleperms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sm.domain.sys.base.roleperms.model.entity.RolePermsEntity;
import sm.domain.sys.base.roleperms.model.vo.RolePermsVO;

import java.util.List;

/**
 * @author Chekfu
 */
@Mapper
public interface RolePermsMapper extends BaseMapper<RolePermsEntity> {
    List<RolePermsVO> selectByRoleId(Long roleId);

    int insertBatch(@Param("entities") List<RolePermsEntity> entities);
}
