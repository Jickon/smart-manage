package sm.cloud.sys.base.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        MenuEntity e = new MenuEntity();
        if (form.getId() != null) {
            e = mapper.selectById(form.getId());
            if (e == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "菜单不存在");
            }
        }
        e.setNumber(form.getNumber());
        e.setName(form.getName());
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
            e.setPath(form.getPath());
            e.setComponent(form.getComponent());
        } else {
            e.setPath(null);
            e.setComponent(null);
        }
        e.setPermissionId(form.getPermissionId());
        e.setLevel(form.getLevel());
        e.setParentId(form.getParentId() != null ? form.getParentId() : 0L);
        e.setAppId(form.getAppId());
        e.setIcon(form.getIcon());
        e.setDescription(form.getDescription());
        e.setSort(form.getSort() != null ? form.getSort() : 99);
        e.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
        if (form.getId() == null) {
            mapper.insert(e);
        } else {
            // 使用全字段 XML 更新，确保分组菜单可以把 permissionId/path/component 清空为 null。
            e.setUpdateTime(LocalDateTime.now());
            e.setUpdateUser(UserHelper.isLogin() ? UserHelper.getCurrentUserId() : null);
            mapper.updateAllColumns(e);
        }
        return e.getId();
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
