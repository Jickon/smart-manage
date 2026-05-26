package sm.cloud.sys.base.cloud.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.cloud.sys.base.cloud.domain.entity.CloudEntity;

@Mapper
public interface CloudMapper extends BaseMapper<CloudEntity> {
}

