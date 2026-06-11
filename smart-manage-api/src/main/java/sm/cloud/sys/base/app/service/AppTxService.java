package sm.cloud.sys.base.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.app.domain.entity.AppEntity;
import sm.cloud.sys.base.app.domain.form.AppSaveForm;
import sm.cloud.sys.base.app.mapper.AppMapper;
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
public class AppTxService {
    private static final String DEFAULT_ICON = "app";
    private static final String DEFAULT_ICON_COLOR = "#165dff";

    private final AppMapper mapper;

    public Long save(AppSaveForm form) {
        AppEntity e;
        if (form.getId() != null) {
            e = mapper.selectById(form.getId());
            if (e == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "应用不存在");
            }
        } else {
            e = new AppEntity();
        }
        e.setName(form.getName());
        e.setNumber(form.getNumber());
        e.setIcon(form.getIcon() == null || form.getIcon().isBlank() ? DEFAULT_ICON : form.getIcon());
        e.setIconColor(form.getIconColor() == null || form.getIconColor().isBlank() ? DEFAULT_ICON_COLOR : form.getIconColor());
        e.setSeq(form.getSeq() != null ? form.getSeq() : 99);
        e.setDescription(form.getDescription());
        e.setCloudId(form.getCloudId());
        e.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
        if (form.getId() == null) {
            mapper.insert(e);
        } else {
            mapper.updateById(e);
        }
        return e.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "应用ID不能为空");
        }
        AppEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "应用不存在");
        }
        mapper.deleteById(id);
    }
}
