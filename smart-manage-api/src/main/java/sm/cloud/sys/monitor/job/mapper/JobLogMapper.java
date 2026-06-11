package sm.cloud.sys.monitor.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.cloud.sys.monitor.job.domain.entity.JobLogEntity;

/**
 * @author Chekfu
 */
@Mapper
public interface JobLogMapper extends BaseMapper<JobLogEntity> {
}
