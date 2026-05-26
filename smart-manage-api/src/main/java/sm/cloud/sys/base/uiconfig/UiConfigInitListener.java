package sm.cloud.sys.base.uiconfig;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sm.cloud.sys.base.app.domain.entity.table.AppTable;
import sm.cloud.sys.base.menu.domain.entity.MenuEntity;
import sm.cloud.sys.base.menu.domain.entity.table.MenuTable;
import sm.cloud.sys.base.menu.mapper.MenuMapper;
import sm.cloud.sys.base.permission.domain.entity.PermissionEntity;
import sm.cloud.sys.base.permission.domain.entity.table.PermissionTable;
import sm.cloud.sys.base.permission.mapper.PermissionMapper;
import sm.cloud.sys.common.enums.MenuLevelEnum;

/**
 * 界面配置模块初始化：自动插入权限和菜单记录（幂等）
 *
 * @author Chekfu
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UiConfigInitListener {
    private final PermissionMapper permissionMapper;
    private final MenuMapper menuMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        var app = Db.selectOneByQuery(
                QueryWrapper.create().from(AppTable.APP).where(AppTable.APP.NUMBER.eq("base")));
        if (app == null) {
            log.info("UiConfigInit: base 应用不存在，跳过界面配置菜单初始化");
            return;
        }
        Long appId = app.getLong(AppTable.APP.ID.getName());

        Long listPageId = ensurePermission("界面配置列表", "sys:base:ui-config:listPage", appId);
        ensurePermission("界面配置详情", "sys:base:ui-config:detail", appId);
        ensurePermission("界面配置保存", "sys:base:ui-config:save", appId);
        ensurePermission("界面配置删除", "sys:base:ui-config:delete", appId);

        // 创建分组（CATEGORY）菜单
        Long categoryId = ensureCategoryMenu("界面配置", listPageId, appId);
        // 创建页面（PAGE）菜单，父级为上述分组
        ensurePageMenu("界面配置管理", listPageId, appId, categoryId);
    }

    private Long ensurePermission(String name, String number, Long appId) {
        var exist = Db.selectOneByQuery(
                QueryWrapper.create().from(PermissionTable.PERMISSION)
                        .where(PermissionTable.PERMISSION.NUMBER.eq(number)));
        if (exist != null) {
            return exist.getLong(PermissionTable.PERMISSION.ID.getName());
        }
        PermissionEntity entity = new PermissionEntity();
        entity.setName(name);
        entity.setNumber(number);
        entity.setAppId(appId);
        permissionMapper.insert(entity);
        log.info("UiConfigInit: 创建权限 {}", number);
        return entity.getId();
    }

    private Long ensureCategoryMenu(String name, Long permissionId, Long appId) {
        var exist = Db.selectOneByQuery(
                QueryWrapper.create().from(MenuTable.MENU)
                        .where(MenuTable.MENU.NAME.eq(name))
                        .and(MenuTable.MENU.LEVEL.eq(MenuLevelEnum.CATEGORY)));
        if (exist != null) {
            return exist.getLong(MenuTable.MENU.ID.getName());
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
        log.info("UiConfigInit: 创建分组菜单 {}", name);
        return entity.getId();
    }

    private void ensurePageMenu(String name, Long permissionId, Long appId, Long parentId) {
        var exist = Db.selectOneByQuery(
                QueryWrapper.create().from(MenuTable.MENU)
                        .where(MenuTable.MENU.PATH.eq("/sys/base/ui-config")));
        if (exist != null) {
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
        log.info("UiConfigInit: 创建页面菜单 sys/base/ui-config");
    }
}
