package sm.domain.sys.monitor.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sm.domain.sys.monitor.job.model.entity.JobEntity;

/**
 * @author Chekfu
 */
@Mapper
public interface JobMapper extends BaseMapper<JobEntity> {
}
