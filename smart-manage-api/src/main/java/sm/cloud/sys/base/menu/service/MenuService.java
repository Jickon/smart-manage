package sm.cloud.sys.base.menu.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.app.domain.entity.AppEntity;
import sm.cloud.sys.base.app.domain.entity.table.AppTable;
import sm.cloud.sys.base.cloud.domain.entity.table.CloudTable;
import sm.system.exception.BizException;
import sm.cloud.sys.base.app.mapper.AppMapper;
import sm.cloud.sys.base.menu.domain.entity.MenuEntity;
import sm.cloud.sys.base.menu.domain.entity.table.MenuTable;
import sm.cloud.sys.base.menu.domain.form.MenuListForm;
import sm.cloud.sys.base.menu.domain.form.MenuSaveForm;
import sm.cloud.sys.base.menu.domain.form.MenuSelectForm;
import sm.cloud.sys.base.menu.domain.vo.MenuCreateNewDataVO;
import sm.cloud.sys.base.menu.domain.vo.MenuDetailVO;
import sm.cloud.sys.base.menu.domain.vo.MenuListVO;
import sm.cloud.sys.base.menu.domain.vo.MenuSelectVO;
import sm.cloud.sys.base.menu.domain.vo.MenuVO;
import sm.cloud.sys.base.menu.mapper.MenuMapper;
import sm.cloud.sys.base.roleperms.domain.entity.table.RolePermsTable;
import sm.cloud.sys.base.userrole.domain.entity.table.UserRoleTable;
import sm.cloud.sys.common.enums.MenuLevelEnum;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MenuService {
	private final MenuMapper mapper;
	private final AppMapper appMapper;

	/**
	 * 前端工作区白名单 key：与路径一致（去前导 /、全小写），例如 "/sys/monitor/home" -> "sys/monitor/home"
	 */
	private static String toWorkspaceComponentKeyByPath(String path) {
		if (path == null) {
			return null;
		}
		String p = path.trim();
		if (p.isEmpty()) {
			return null;
		}
		if (p.startsWith("/")) {
			p = p.substring(1);
		}
		if (p.isEmpty()) {
			return null;
		}
		return p.toLowerCase();
	}

	public PageResult<MenuListVO> listPage(MenuListForm form) {
		QueryWrapper qw = QueryWrapper.create().from(MenuTable.MENU);
		if (form.getAppId() != null) {
			qw.and(MenuTable.MENU.APP_ID.eq(form.getAppId()));
		}
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(MenuTable.MENU.NAME.like(kw).or(MenuTable.MENU.PATH.like(kw)));
		}
		qw.orderBy(MenuTable.MENU.SORT, true).orderBy(MenuTable.MENU.ID, true);
		Page<MenuEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<MenuEntity> result = mapper.paginate(page, qw);
		List<MenuListVO> vos = result.getRecords().stream().map(this::toMenuListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
	}

	private MenuListVO toMenuListVo(MenuEntity e) {
		MenuListVO vo = new MenuListVO();
		vo.setId(e.getId());
		vo.setNumber(e.getNumber());
		vo.setLevel(e.getLevel());
		vo.setParentId(e.getParentId());
		vo.setName(e.getName());
		vo.setPath(e.getPath());
		vo.setComponent(e.getComponent());
		vo.setSort(e.getSort());
		vo.setIcon(e.getIcon());
		return vo;
	}

	/**
	 * 基础资料选择：分页查询菜单。
	 * 支持按应用、层级、排除自身、是否启用、关键词过滤；按 sort、id 排序。
	 */
	public PageResult<MenuSelectVO> select(MenuSelectForm form) {
		QueryWrapper qw = QueryWrapper.create().from(MenuTable.MENU);
		if (form.getAppId() != null) {
			qw.and(MenuTable.MENU.APP_ID.eq(form.getAppId()));
		}
		if (form.getLevel() != null) {
			qw.and(MenuTable.MENU.LEVEL.eq(form.getLevel()));
		}
		if (form.getExcludeId() != null) {
			qw.and(MenuTable.MENU.ID.ne(form.getExcludeId()));
		}
		if (form.getEnableFlag() != null) {
			qw.and(MenuTable.MENU.ENABLE_FLAG.eq(form.getEnableFlag()));
		}
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(MenuTable.MENU.NUMBER.like(kw).or(MenuTable.MENU.NAME.like(kw)));
		}
		qw.orderBy(MenuTable.MENU.SORT, true).orderBy(MenuTable.MENU.ID, true);
		Page<MenuEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<MenuEntity> result = mapper.paginate(page, qw);
		List<MenuSelectVO> vos = result.getRecords().stream().map(this::toMenuSelectVo).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
	}

	private MenuSelectVO toMenuSelectVo(MenuEntity e) {
		MenuSelectVO vo = new MenuSelectVO();
		vo.setId(e.getId());
		vo.setNumber(e.getNumber());
		vo.setName(e.getName());
		vo.setLevel(e.getLevel());
		vo.setEnableFlag(e.getEnableFlag());
		return vo;
	}

	/**
	 * 获取用户的云应用
	 */
	public MenuVO getUserApps(Long userId) {
		throw new UnsupportedOperationException("getUserApps 暂未实现");
	}

	/**
	 * 获取应用下的菜单
	 */
	//	@Cacheable(cacheNames = "user_menu", key = "#userId + ':' + #appId")
	public MenuVO getUserMenusByAppId(Long userId, Long appId) {
		MenuVO root = new MenuVO();
		root.setRoutes(new ArrayList<>());

		if (userId == null || appId == null) {
			return root;
		}

		// 应用基础信息（root 不再依赖 level=APP 的菜单记录）
		Row appRow = Db.selectOneByQuery(
				QueryWrapper.create()
						.select(
								AppTable.APP.NAME.as("aname"),
								AppTable.APP.NUMBER.as("anumber"),
								AppTable.APP.ICON.as("aicon"),
								CloudTable.CLOUD.NUMBER.as("cnumber")
						)
						.from(AppTable.APP)
						.leftJoin(CloudTable.CLOUD).on(CloudTable.CLOUD.ID.eq(AppTable.APP.CLOUD_ID))
						.where(AppTable.APP.ID.eq(appId))
						.limit(1)
		);
		if (appRow != null) {
			final String appName = appRow.getString("aname");
			final String appNumber = appRow.getString("anumber");
			final String cloudNumber = appRow.getString("cnumber");
			root.setName(appName);
			root.setIcon(appRow.getString("aicon"));

			// 关键：AppWorkspaceLayout 只有在 rootMenu.path 有值时才渲染侧边栏菜单
			if (cloudNumber != null && !cloudNumber.isBlank() && appNumber != null && !appNumber.isBlank()) {
				root.setPath("/" + cloudNumber + "/" + appNumber + "/home");
				root.setComponent(toWorkspaceComponentKeyByPath(root.getPath()));
			}
		}

			// 超级管理员跳过权限过滤，直接查全量菜单
			QueryWrapper query;
			boolean isAdmin = UserHelper.isAdmin();
			if (isAdmin) {
				query = QueryWrapper.create()
						.select(MenuTable.MENU.ALL_COLUMNS)
						.from(MenuTable.MENU)
						.where(MenuTable.MENU.APP_ID.eq(appId))
						.and(MenuTable.MENU.LEVEL.in(MenuLevelEnum.CATEGORY, MenuLevelEnum.PAGE));
			} else {
				query = QueryWrapper.create()
						.select(MenuTable.MENU.ALL_COLUMNS)
						.leftJoin(RolePermsTable.ROLE_PERMS).on(RolePermsTable.ROLE_PERMS.PERMISSION_ID.eq(MenuTable.MENU.PERMISSION_ID))
						.leftJoin(UserRoleTable.USER_ROLE).on(UserRoleTable.USER_ROLE.ROLE_ID.eq(RolePermsTable.ROLE_PERMS.ROLE_ID))
						.where(MenuTable.MENU.APP_ID.eq(appId))
						.and(UserRoleTable.USER_ROLE.USER_ID.eq(userId))
						.and(MenuTable.MENU.LEVEL.in(MenuLevelEnum.CATEGORY, MenuLevelEnum.PAGE));
			}
			query.orderBy(MenuTable.MENU.LEVEL.asc(), MenuTable.MENU.SORT.asc(), MenuTable.MENU.CREATE_TIME.asc());
		List<MenuEntity> menuEntities = mapper.selectListByQueryAs(query, MenuEntity.class);

		Map<Long, MenuVO> cacheCategory = new HashMap<>();
		for (MenuEntity menuEntity : menuEntities) {
			if (menuEntity.getLevel().equals(MenuLevelEnum.CATEGORY)) {
				MenuVO category = new MenuVO();
				category.setName(menuEntity.getName());
				category.setPath(menuEntity.getPath());
				category.setComponent(menuEntity.getComponent());
				category.setIcon(menuEntity.getIcon());
				category.setLevel(MenuLevelEnum.CATEGORY);
				root.getRoutes().add(category);
				cacheCategory.put(menuEntity.getId(), category);
			} else if (menuEntity.getLevel().equals(MenuLevelEnum.PAGE)) {
				MenuVO page = new MenuVO();
				page.setName(menuEntity.getName());
				page.setPath(menuEntity.getPath());
				page.setComponent(menuEntity.getComponent());
				page.setIcon(menuEntity.getIcon());
				page.setLevel(MenuLevelEnum.PAGE);
				MenuVO parent = cacheCategory.get(menuEntity.getParentId());
				if (parent == null) {
					continue;
				}
				if (parent.getRoutes() == null) {
					parent.setRoutes(new ArrayList<>());
				}
				parent.getRoutes().add(page);
			}
		}
		return root;
	}

	/**
	 * 按应用编号（t_sys_app.number）获取当前用户在应用下的菜单树。
	 * 返回空 root（routes=[]）表示应用不存在或无权限菜单。
	 */
	public MenuVO getUserMenusByAppNumber(Long userId, String appNumber) {
		if (appNumber == null || appNumber.isBlank()) {
			MenuVO empty = new MenuVO();
			empty.setRoutes(new ArrayList<>());
			return empty;
		}
		AppEntity app = appMapper.selectOneByQueryAs(
				QueryWrapper.create()
						.from(AppTable.APP)
						.where(AppTable.APP.NUMBER.eq(appNumber))
						.limit(1),
				AppEntity.class
		);
		Long appId = app == null ? null : app.getId();
		if (appId == null) {
			MenuVO empty = new MenuVO();
			empty.setRoutes(new ArrayList<>());
			return empty;
		}
		return getUserMenusByAppId(userId, appId);
	}

	public MenuEntity getById(Long id) {
		return mapper.selectOneById(id);
	}

	public MenuDetailVO getDetail(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "菜单ID不能为空");
		}
		MenuEntity entity = mapper.selectOneById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "菜单不存在");
		}
		MenuDetailVO vo = toDetailVo(entity);
		// 填充父菜单信息
		if (entity.getParentId() != null && entity.getParentId() > 0) {
			MenuEntity parentEntity = mapper.selectOneById(entity.getParentId());
			if (parentEntity != null) {
				MenuDetailVO.ParentInfo info = new MenuDetailVO.ParentInfo();
				info.setId(String.valueOf(parentEntity.getId()));
				info.setNumber(parentEntity.getNumber());
				info.setName(parentEntity.getName());
				vo.setParent(info);
			}
		}
		return vo;
	}

	private MenuDetailVO toDetailVo(MenuEntity entity) {
		MenuDetailVO vo = new MenuDetailVO();
		vo.setId(String.valueOf(entity.getId()));
		vo.setNumber(entity.getNumber());
		vo.setName(entity.getName());
		vo.setLevel(entity.getLevel());
		vo.setAppId(entity.getAppId());
		vo.setPermissionId(entity.getPermissionId());
		vo.setPath(entity.getPath());
		vo.setComponent(entity.getComponent());
		vo.setIcon(entity.getIcon());
		vo.setDescription(entity.getDescription());
		vo.setSort(entity.getSort());
		vo.setEnableFlag(entity.getEnableFlag());
		vo.setCreateTime(entity.getCreateTime());
		vo.setUpdateTime(entity.getUpdateTime());
		vo.setCreateUser(entity.getCreateUser());
		vo.setUpdateUser(entity.getUpdateUser());
		return vo;
	}

	public MenuCreateNewDataVO createNewData() {
		MenuCreateNewDataVO vo = new MenuCreateNewDataVO();
		vo.setParentId(0L);
		vo.setSort(99);
		vo.setEnableFlag(true);
		return vo;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long save(MenuSaveForm form) {
		MenuEntity e = new MenuEntity();
		if (form.getId() != null) {
			e = mapper.selectOneById(form.getId());
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
			// forceUpdate=false：允许 null 值覆盖到数据库，分组菜单需清空 permissionId/path/component
			mapper.update(e, false);
		}
		return e.getId();
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "菜单ID不能为空");
		}
		MenuEntity entity = mapper.selectOneById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "菜单不存在");
		}
		mapper.deleteById(id);
	}
}
