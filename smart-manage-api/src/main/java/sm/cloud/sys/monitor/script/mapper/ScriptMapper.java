package sm.cloud.sys.monitor.script.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.cloud.sys.monitor.script.domain.entity.ScriptEntity;

@Mapper
public interface ScriptMapper extends BaseMapper<ScriptEntity> {
}
