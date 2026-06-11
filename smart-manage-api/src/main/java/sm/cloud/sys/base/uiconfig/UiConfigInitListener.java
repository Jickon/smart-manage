package sm.cloud.sys.base.uiconfig;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sm.cloud.sys.base.app.domain.entity.AppEntity;
import sm.cloud.sys.base.app.mapper.AppMapper;
import sm.cloud.sys.base.menu.domain.entity.MenuEntity;
import sm.cloud.sys.base.menu.mapper.MenuMapper;
import sm.cloud.sys.base.permission.domain.entity.PermissionEntity;
import sm.cloud.sys.base.permission.mapper.PermissionMapper;
import sm.cloud.sys.common.enums.MenuLevelEnum;

/**
 * 界面配置模块初始化。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UiConfigInitListener {
    private final AppMapper appMapper;
    private final PermissionMapper permissionMapper;
    private final MenuMapper menuMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        AppEntity app = appMapper.selectOne(new LambdaQueryWrapper<AppEntity>()
                .eq(AppEntity::getNumber, "base"));
        if (app == null) {
            log.info("UiConfigInit: base 应用不存在，跳过初始化");
            return;
        }
        Long listPageId = ensurePermission("界面配置列表", "sys:base:ui-config:listPage", app.getId());
        ensurePermission("界面配置详情", "sys:base:ui-config:detail", app.getId());
        ensurePermission("界面配置保存", "sys:base:ui-config:save", app.getId());
        ensurePermission("界面配置删除", "sys:base:ui-config:delete", app.getId());
        Long categoryId = ensureCategoryMenu("界面配置", listPageId, app.getId());
        ensurePageMenu("界面配置管理", listPageId, app.getId(), categoryId);
    }

    private Long ensurePermission(String name, String number, Long appId) {
        PermissionEntity existing = permissionMapper.selectOne(new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getNumber, number));
        if (existing != null) {
            return existing.getId();
        }
        PermissionEntity entity = new PermissionEntity();
        entity.setName(name);
        entity.setNumber(number);
        entity.setAppId(appId);
        permissionMapper.insert(entity);
        return entity.getId();
    }

    private Long ensureCategoryMenu(String name, Long permissionId, Long appId) {
        MenuEntity existing = menuMapper.selectOne(new LambdaQueryWrapper<MenuEntity>()
                .eq(MenuEntity::getName, name)
                .eq(MenuEntity::getLevel, MenuLevelEnum.CATEGORY));
        if (existing != null) {
            return existing.getId();
        }
        MenuEntity entity = new MenuEntity();
        entity.setNumber("UI_CONFIG_CAT");
        entity.setName(name);
        entity.setLevel(MenuLevelEnum.CATEGORY);
        entity.setParentId(0L);
        entity.setAppId(appId);
        entity.setPermissionId(permissionId);
        entity.setIcon("setting");
        entity.setSort(80);
        entity.setEnableFlag(true);
        menuMapper.insert(entity);
        return entity.getId();
    }

    private void ensurePageMenu(String name, Long permissionId, Long appId, Long parentId) {
        MenuEntity existing = menuMapper.selectOne(new LambdaQueryWrapper<MenuEntity>()
                .eq(MenuEntity::getPath, "/sys/base/ui-config"));
        if (existing != null) {
            return;
        }
        MenuEntity entity = new MenuEntity();
        entity.setNumber("UI_CONFIG_PAGE");
        entity.setName(name);
        entity.setLevel(MenuLevelEnum.PAGE);
        entity.setParentId(parentId);
        entity.setAppId(appId);
        entity.setPermissionId(permissionId);
        entity.setPath("/sys/base/ui-config");
        entity.setComponent("sys/base/ui-config");
        entity.setIcon("setting");
        entity.setSort(10);
        entity.setEnableFlag(true);
        menuMapper.insert(entity);
    }
}
