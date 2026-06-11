package sm.cloud.sys.monitor.sql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.cloud.sys.monitor.sql.domain.entity.SqlLogEntity;

@Mapper
public interface SqlLogMapper extends BaseMapper<SqlLogEntity> {
}
