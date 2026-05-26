package sm.cloud.sys.monitor.loginlog.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.cloud.sys.monitor.loginlog.domain.entity.LoginLogEntity;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLogEntity> {
}

