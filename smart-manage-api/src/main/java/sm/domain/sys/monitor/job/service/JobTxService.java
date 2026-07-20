package sm.domain.sys.monitor.job.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.monitor.job.mapper.JobLogMapper;
import sm.domain.sys.monitor.job.mapper.JobMapper;
import sm.domain.sys.monitor.job.model.entity.JobEntity;
import sm.domain.sys.monitor.job.model.entity.JobLogEntity;
import sm.domain.sys.monitor.job.model.form.JobSaveForm;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/** 定时任务数据库事务服务；Quartz 同步由公开 Service 在事务提交后执行。 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class JobTxService {

    private final JobMapper mapper;
    private final JobLogMapper jobLogMapper;

    public Long save(JobSaveForm form) {
        JobEntity entity;
        if (form.getId() == null) {
            entity = new JobEntity();
            entity.setNumber(form.getNumber());
        } else {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "任务不存在");
            }
            if (Boolean.TRUE.equals(entity.getIsSystem())) {
                if (!entity.getJobClassName().equals(form.getJobClassName())) {
                    throw new BizException(ResultEnum.BILL_STATUS_ERROR, "系统内置任务不可修改执行类");
                }
                if (!entity.getJobGroup().equals(form.getJobGroup())) {
                    throw new BizException(ResultEnum.BILL_STATUS_ERROR, "系统内置任务不可修改任务分组");
                }
            }
        }

        entity.setJobName(form.getJobName());
        entity.setJobGroup(form.getJobGroup() != null ? form.getJobGroup() : "DEFAULT");
        entity.setJobClassName(form.getJobClassName());
        entity.setCronExpression(form.getCronExpression());
        entity.setJobData(form.getJobData());
        entity.setStatus(form.getStatus() != null ? form.getStatus() : "ENABLED");
        entity.setRemark(form.getRemark());

        if (form.getId() == null) {
            if (mapper.insert(entity) != 1) {
                throw new BizException(ResultEnum.PERSISTENCE_ERROR, "新增任务失败");
            }
        } else if (mapper.updateById(entity) != 1) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "任务已被其他用户修改");
        }
        return entity.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "任务ID不能为空");
        }
        JobEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "任务不存在");
        }
        if (Boolean.TRUE.equals(entity.getIsSystem())) {
            throw new BizException(ResultEnum.BILL_STATUS_ERROR, "系统内置任务不可删除");
        }
        jobLogMapper.delete(new LambdaQueryWrapper<JobLogEntity>().eq(JobLogEntity::getJobId, id));
        if (mapper.deleteById(id) != 1) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "任务已被其他用户删除");
        }
    }

    public void pause(Long id) {
        updateStatus(id, "PAUSED");
    }

    public void resume(Long id) {
        updateStatus(id, "ENABLED");
    }

    private void updateStatus(Long id, String status) {
        JobEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "任务不存在");
        }
        entity.setStatus(status);
        if (mapper.updateById(entity) == 0) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "任务状态已被其他用户修改");
        }
    }
}
