package sm.domain.sys.base.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.domain.sys.base.cloud.model.entity.CloudEntity;

@Mapper
public interface CloudMapper extends BaseMapper<CloudEntity> {
}

