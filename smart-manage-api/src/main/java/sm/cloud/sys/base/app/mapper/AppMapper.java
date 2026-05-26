package sm.cloud.sys.base.app.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.cloud.sys.base.app.domain.entity.AppEntity;

@Mapper
public interface AppMapper extends BaseMapper<AppEntity> {
}

