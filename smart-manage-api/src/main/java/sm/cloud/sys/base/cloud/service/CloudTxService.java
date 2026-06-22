package sm.cloud.sys.base.cloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.cloud.domain.entity.CloudEntity;
import sm.cloud.sys.base.cloud.domain.form.CloudSaveForm;
import sm.cloud.sys.base.cloud.mapper.CloudMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 云事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CloudTxService {
    private final CloudMapper mapper;

    public Long save(CloudSaveForm form) {
        CloudEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "云不存在");
            }
        } else {
            entity = new CloudEntity();
        }
        entity.setName(form.getName());
        entity.setNumber(form.getNumber());
        entity.setSeq(form.getSeq() != null ? form.getSeq() : 99);
        entity.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
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
        mapper.deleteById(id);
    }
}
