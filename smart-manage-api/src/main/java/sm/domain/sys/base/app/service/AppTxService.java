package sm.domain.sys.base.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.app.model.entity.AppEntity;
import sm.domain.sys.base.app.model.form.AppSaveForm;
import sm.domain.sys.base.app.mapper.AppMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 应用事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class AppTxService {
    private static final String DEFAULT_ICON = "app";
    private static final String DEFAULT_ICON_COLOR = "#165dff";

    private final AppMapper mapper;

    public Long save(AppSaveForm form) {
        AppEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "应用不存在");
            }
        } else {
            entity = new AppEntity();
        }
        entity.setName(form.getName());
        entity.setNumber(form.getNumber());
        entity.setIcon(form.getIcon() == null || form.getIcon().isBlank() ? DEFAULT_ICON : form.getIcon());
        entity.setIconColor(form.getIconColor() == null || form.getIconColor().isBlank() ? DEFAULT_ICON_COLOR : form.getIconColor());
        entity.setSeq(form.getSeq() != null ? form.getSeq() : 99);
        entity.setDescription(form.getDescription());
        entity.setCloudId(form.getCloudId());
        entity.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            if (mapper.updateById(entity) == 0) {
                throw new BizException(ResultEnum.DATA_CONFLICT, "应用已被其他用户修改，请刷新后重试");
            }
        }
        return entity.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "应用ID不能为空");
        }
        AppEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "应用不存在");
        }
        if (mapper.deleteById(id) == 0) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "应用已被其他用户删除");
        }
    }
}
