package sm.cloud.sys.base.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.menu.domain.entity.MenuEntity;
import sm.cloud.sys.base.menu.domain.form.MenuSaveForm;
import sm.cloud.sys.base.menu.mapper.MenuMapper;
import sm.cloud.sys.common.enums.MenuLevelEnum;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.time.LocalDateTime;

/**
 * 菜单事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MenuTxService {
    private final MenuMapper mapper;

    public Long save(MenuSaveForm form) {
        MenuEntity entity = new MenuEntity();
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "菜单不存在");
            }
        }
        entity.setNumber(form.getNumber());
        entity.setName(form.getName());
        if (form.getLevel() == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "菜单层级不能为空");
        }
        // 菜单层级收敛为两级：分组/页面
        if (!(form.getLevel().equals(MenuLevelEnum.CATEGORY) || form.getLevel().equals(MenuLevelEnum.PAGE))) {
            throw new BizException(ResultEnum.PARAM_ERROR, "菜单层级只能是分组或页面");
        }
        // 页面层级必须指定权限和路径；分组层级不需要路径和组件，但保留权限用于菜单可见性控制
        if (form.getLevel().equals(MenuLevelEnum.PAGE)) {
            if (form.getPermissionId() == null) {
                throw new BizException("页面层级菜单必须选择权限");
            }
            if (form.getPath() == null || form.getPath().isBlank()) {
                throw new BizException("页面层级菜单必须填写路径");
            }
            entity.setPath(form.getPath());
            entity.setComponent(form.getComponent());
        } else {
            entity.setPath(null);
            entity.setComponent(null);
        }
        entity.setPermissionId(form.getPermissionId());
        entity.setLevel(form.getLevel());
        entity.setParentId(form.getParentId() != null ? form.getParentId() : 0L);
        entity.setAppId(form.getAppId());
        entity.setIcon(form.getIcon());
        entity.setDescription(form.getDescription());
        entity.setSort(form.getSort() != null ? form.getSort() : 99);
        entity.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            // 使用全字段 XML 更新，确保分组菜单可以把 permissionId/path/component 清空为 null。
            entity.setUpdateTime(LocalDateTime.now());
            entity.setUpdateUser(UserHelper.isLogin() ? UserHelper.getCurrentUserId() : null);
            mapper.updateAllColumns(entity);
        }
        return entity.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "菜单ID不能为空");
        }
        MenuEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "菜单不存在");
        }
        mapper.deleteById(id);
    }
}
