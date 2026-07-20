package sm.domain.sys.monitor.script.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.monitor.script.mapper.ScriptMapper;
import sm.domain.sys.monitor.script.model.entity.ScriptEntity;
import sm.domain.sys.monitor.script.model.form.ScriptSaveForm;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 脚本事务服务，仅供同一单据的 {@link ScriptService} 委托调用。
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class ScriptTxService {

    private final ScriptMapper mapper;

    public Long save(ScriptSaveForm form) {
        ScriptEntity entity;
        if (form.getId() == null) {
            entity = new ScriptEntity();
        } else {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "脚本不存在");
            }
        }

        entity.setNumber(form.getNumber());
        entity.setName(form.getName());
        entity.setContent(form.getContent());
        entity.setRemark(form.getRemark());

        if (form.getId() == null) {
            if (mapper.insert(entity) != 1) {
                throw new BizException(sm.system.response.ResultEnum.PERSISTENCE_ERROR, "新增数据失败");
            }
        } else if (mapper.updateById(entity) == 0) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "脚本已被其他用户修改");
        }
        return entity.getId();
    }

    public void delete(Long id) {
        if (mapper.selectById(id) == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "脚本不存在");
        }
        if (mapper.deleteById(id) == 0) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "脚本已被删除");
        }
    }
}
