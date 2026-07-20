package sm.domain.sys.base.uiconfig.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.uiconfig.model.entity.UiConfigEntity;
import sm.domain.sys.base.uiconfig.model.form.UiConfigSaveForm;
import sm.domain.sys.base.uiconfig.mapper.UiConfigMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 界面配置事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class UiConfigTxService {
    private final UiConfigMapper mapper;

    /** 新增/编辑，清除缓存 */
    public Long save(UiConfigSaveForm form) {
        UiConfigEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "界面配置不存在");
            }
        } else {
            entity = new UiConfigEntity();
        }
        entity.setPageTitle(form.getPageTitle());
        entity.setSystemName(form.getSystemName());
        entity.setLoginBanner(form.getLoginBanner());
        entity.setLoginLogo(form.getLoginLogo());
        entity.setHeaderLogo(form.getHeaderLogo());
        if (form.getId() == null) {
            if (mapper.insert(entity) != 1) {
                throw new BizException(sm.system.response.ResultEnum.PERSISTENCE_ERROR, "新增数据失败");
            }
        } else {
            if (mapper.updateById(entity) != 1) {
                throw new BizException(sm.system.response.ResultEnum.DATA_CONFLICT, "数据已被其他用户修改");
            }
        }
        return entity.getId();
    }

    /** 删除，清除缓存 */
    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "界面配置ID不能为空");
        }
        UiConfigEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "界面配置不存在");
        }
        if (mapper.deleteById(id) != 1) {
            throw new BizException(sm.system.response.ResultEnum.DATA_CONFLICT, "数据已被其他用户删除");
        }
    }
}
