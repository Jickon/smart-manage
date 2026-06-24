package sm.domain.sys.base.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sm.domain.sys.base.permission.model.entity.PermissionEntity;

import java.util.List;

/**
 * @author Chekfu
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {
    List<String> selectUserPermissionNumbers(@Param("userId") Long userId,
                                             @Param("orgId") Long orgId,
                                             @Param("prefix") String prefix);
}
