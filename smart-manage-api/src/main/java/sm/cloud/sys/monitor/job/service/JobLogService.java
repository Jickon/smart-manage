package sm.cloud.sys.monitor.job.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.monitor.job.domain.entity.JobLogEntity;
import sm.cloud.sys.monitor.job.domain.entity.table.JobLogTable;
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
        QueryWrapper qw = QueryWrapper.create().from(JobLogTable.JOB_LOG);
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            qw.and(JobLogTable.JOB_LOG.JOB_NAME.like("%" + form.getKeyword().trim() + "%"));
        }
        if (form.getStatus() != null && !form.getStatus().isBlank()) {
            qw.and(JobLogTable.JOB_LOG.STATUS.eq(form.getStatus()));
        }
        if (form.getJobId() != null) {
            qw.and(JobLogTable.JOB_LOG.JOB_ID.eq(form.getJobId()));
        }
        qw.orderBy(JobLogTable.JOB_LOG.START_TIME, false);

        Page<JobLogEntity> page = Page.of(form.getPageNum(), form.getPageSize());
        Page<JobLogEntity> result = mapper.paginate(page, qw);
        List<JobLogListVO> vos = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(result.getTotalRow(), vos);
    }

    public List<JobLogListVO> running() {
        QueryWrapper qw = QueryWrapper.create().from(JobLogTable.JOB_LOG)
                .where(JobLogTable.JOB_LOG.STATUS.eq("RUNNING"))
                .orderBy(JobLogTable.JOB_LOG.START_TIME, false);
        List<JobLogEntity> list = mapper.selectListByQuery(qw);
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
