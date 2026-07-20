package sm.domain.sys.base.cloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.cloud.model.entity.CloudEntity;
import sm.domain.sys.base.cloud.model.form.CloudSaveForm;
import sm.domain.sys.base.cloud.mapper.CloudMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;
import sm.system.util.EnabledCommandUtil;

import java.util.List;

/**
 * 云事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class CloudTxService {
    private final CloudMapper mapper;

    public Long save(CloudSaveForm form) {
        CloudEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "云不存在");
            }
            if (form.getVersion() == null) {
                throw new BizException(ResultEnum.PARAM_ERROR, "修改云时乐观锁版本号不能为空");
            }
            if (!java.util.Objects.equals(entity.getVersion(), form.getVersion())) {
                throw new BizException(ResultEnum.DATA_CONFLICT, "云已被其他用户修改，请刷新后重试");
            }
        } else {
            entity = new CloudEntity();
        }
        entity.setName(form.getName());
        entity.setNumber(form.getNumber());
        entity.setSeq(form.getSeq() != null ? form.getSeq() : 99);
        if (form.getId() == null) {
            entity.setEnabled(true);
            if (mapper.insert(entity) != 1) {
                throw new BizException(sm.system.response.ResultEnum.PERSISTENCE_ERROR, "新增数据失败");
            }
        } else {
            if (mapper.updateById(entity) == 0) {
                throw new BizException(ResultEnum.DATA_CONFLICT, "云已被其他用户修改，请刷新后重试");
            }
        }
        return entity.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "云ID不能为空");
        }
        CloudEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "云不存在");
        }
        if (mapper.deleteById(id) == 0) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "云已被其他用户删除");
        }
    }

    public void updateEnabled(List<Long> ids, boolean enabled) {
        EnabledCommandUtil.update(mapper, CloudEntity::getId, CloudEntity::getEnabled, ids, enabled, "云");
    }
}
