package sm.cloud.sys.base.sysparam.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.sysparam.domain.entity.SysParamEntity;
import sm.cloud.sys.base.sysparam.domain.form.SysParamSaveForm;
import sm.cloud.sys.base.sysparam.mapper.SysParamMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 系统参数事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SysParamTxService {
    private final SysParamMapper mapper;

    /** 新增/编辑，清除缓存 */
    @CacheInvalidate(name = "sys-params", key = "'all'")
    public Long save(SysParamSaveForm form) {
        SysParamEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "系统参数不存在");
            }
            // 系统内置参数只允许修改 value
            if (Boolean.TRUE.equals(entity.getIsSystem())) {
                entity.setValue(form.getValue());
                mapper.updateById(entity);
                return entity.getId();
            }
        } else {
            entity = new SysParamEntity();
        }
        entity.setNumber(form.getNumber());
        entity.setName(form.getName());
        entity.setValue(form.getValue());
        entity.setRemark(form.getRemark());
        if (form.getId() == null) {
            entity.setIsSystem(false);
        }
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return entity.getId();
    }

    /** 删除，清除缓存 */
    @CacheInvalidate(name = "sys-params", key = "'all'")
    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "系统参数ID不能为空");
        }
        SysParamEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "系统参数不存在");
        }
        if (Boolean.TRUE.equals(entity.getIsSystem())) {
            throw new BizException("系统内置参数不可删除");
        }
        mapper.deleteById(id);
    }
}
