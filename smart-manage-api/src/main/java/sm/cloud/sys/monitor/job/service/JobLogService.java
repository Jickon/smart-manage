package sm.cloud.sys.monitor.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.monitor.job.domain.entity.JobLogEntity;
import sm.cloud.sys.monitor.job.domain.form.JobLogListForm;
import sm.cloud.sys.monitor.job.domain.vo.JobLogListVO;
import sm.cloud.sys.monitor.job.mapper.JobLogMapper;
import sm.system.response.PageResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行实例/执行日志 Service
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JobLogService {

    private final JobLogMapper mapper;

    public PageResult<JobLogListVO> listPage(JobLogListForm form) {
        LambdaQueryWrapper<JobLogEntity> qw = new LambdaQueryWrapper<JobLogEntity>();
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            qw.like(JobLogEntity::getJobName, "%" + form.getKeyword().trim() + "%");
        }
        if (form.getStatus() != null && !form.getStatus().isBlank()) {
            qw.eq(JobLogEntity::getStatus, form.getStatus());
        }
        if (form.getJobId() != null) {
            qw.eq(JobLogEntity::getJobId, form.getJobId());
        }
        qw.orderByDesc(JobLogEntity::getStartTime);

        Page<JobLogEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
        Page<JobLogEntity> result = mapper.selectPage(page, qw);
        List<JobLogListVO> vos = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(result.getTotal(), vos);
    }

    public List<JobLogListVO> running() {
        LambdaQueryWrapper<JobLogEntity> qw = new LambdaQueryWrapper<JobLogEntity>()
                .eq(JobLogEntity::getStatus, "RUNNING")
                .orderByDesc(JobLogEntity::getStartTime);
        List<JobLogEntity> list = mapper.selectList(qw);
        return list.stream().map(this::toVo).collect(Collectors.toList());
    }

    private JobLogListVO toVo(JobLogEntity entity) {
        JobLogListVO vo = new JobLogListVO();
        vo.setId(entity.getId());
        vo.setJobId(entity.getJobId());
        vo.setJobName(entity.getJobName());
        vo.setJobGroup(entity.getJobGroup());
        vo.setStartTime(entity.getStartTime());
        vo.setEndTime(entity.getEndTime());
        vo.setDurationMs(entity.getDurationMs());
        vo.setStatus(entity.getStatus());
        vo.setErrorMessage(entity.getErrorMessage());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
