package sm.cloud.sys.monitor.operatelog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.cloud.sys.monitor.operatelog.domain.entity.OperateLogEntity;

@Mapper
public interface OperateLogMapper extends BaseMapper<OperateLogEntity> {
}

