package sm.domain.sys.monitor.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.domain.sys.monitor.job.model.entity.JobLogEntity;

/**
 * @author Chekfu
 */
@Mapper
public interface JobLogMapper extends BaseMapper<JobLogEntity> {
}
