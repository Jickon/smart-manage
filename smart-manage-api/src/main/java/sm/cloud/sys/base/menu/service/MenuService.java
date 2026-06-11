package sm.cloud.sys.base.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.app.domain.entity.AppEntity;
import sm.system.exception.BizException;
import sm.cloud.sys.base.app.mapper.AppMapper;
import sm.cloud.sys.base.menu.domain.entity.MenuEntity;
import sm.cloud.sys.base.menu.domain.form.MenuListForm;
import sm.cloud.sys.base.menu.domain.form.MenuSaveForm;
import sm.cloud.sys.base.menu.domain.form.MenuSelectForm;
import sm.cloud.sys.base.menu.domain.vo.MenuCreateNewDataVO;
import sm.cloud.sys.base.menu.domain.vo.MenuDetailVO;
import sm.cloud.sys.base.menu.domain.vo.MenuListVO;
import sm.cloud.sys.base.menu.domain.vo.MenuAppInfoVO;
import sm.cloud.sys.base.menu.domain.vo.MenuSelectVO;
import sm.cloud.sys.base.menu.domain.vo.MenuVO;
import sm.cloud.sys.base.menu.mapper.MenuMapper;
import sm.cloud.sys.common.enums.MenuLevelEnum;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
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
		LambdaQueryWrapper<MenuEntity> wrapper = new LambdaQueryWrapper<MenuEntity>();
		wrapper.eq(form.getAppId() != null, MenuEntity::getAppId, form.getAppId());
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String keyword = form.getKeyword().trim();
			wrapper.and(condition -> condition.like(MenuEntity::getName, keyword)
					.or().like(MenuEntity::getPath, keyword));
		}
		wrapper.orderByAsc(MenuEntity::getSort).orderByAsc(MenuEntity::getId);
		Page<MenuEntity> result = mapper.selectPage(new Page<>(form.getPageNum(), form.getPageSize()), wrapper);
		List<MenuListVO> records = result.getRecords().stream().map(this::toMenuListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), records);
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
		LambdaQueryWrapper<MenuEntity> wrapper = new LambdaQueryWrapper<MenuEntity>();
		wrapper.eq(form.getAppId() != null, MenuEntity::getAppId, form.getAppId())
				.eq(form.getLevel() != null, MenuEntity::getLevel, form.getLevel())
				.ne(form.getExcludeId() != null, MenuEntity::getId, form.getExcludeId())
				.eq(form.getEnableFlag() != null, MenuEntity::getEnableFlag, form.getEnableFlag());
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String keyword = form.getKeyword().trim();
			wrapper.and(condition -> condition.like(MenuEntity::getNumber, keyword)
					.or().like(MenuEntity::getName, keyword));
		}
		wrapper.orderByAsc(MenuEntity::getSort).orderByAsc(MenuEntity::getId);
		Page<MenuEntity> result = mapper.selectPage(new Page<>(form.getPageNum(), form.getPageSize()), wrapper);
		List<MenuSelectVO> records = result.getRecords().stream().map(this::toMenuSelectVo).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), records);
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

		MenuAppInfoVO appInfo = mapper.selectAppInfo(appId);
		if (appInfo != null) {
			root.setName(appInfo.getAppName());
			root.setIcon(appInfo.getAppIcon());
			if (appInfo.getCloudNumber() != null && !appInfo.getCloudNumber().isBlank()
					&& appInfo.getAppNumber() != null && !appInfo.getAppNumber().isBlank()) {
				root.setPath("/" + appInfo.getCloudNumber() + "/" + appInfo.getAppNumber() + "/home");
				root.setComponent(toWorkspaceComponentKeyByPath(root.getPath()));
			}
		}

		List<MenuEntity> menuEntities = mapper.selectUserMenus(userId, appId, UserHelper.isAdmin());
		Map<Long, MenuVO> categories = new HashMap<>();
		for (MenuEntity menuEntity : menuEntities) {
			MenuVO menu = new MenuVO();
			menu.setName(menuEntity.getName());
			menu.setPath(menuEntity.getPath());
			menu.setComponent(menuEntity.getComponent());
			menu.setIcon(menuEntity.getIcon());
			menu.setLevel(menuEntity.getLevel());
			if (MenuLevelEnum.CATEGORY.equals(menuEntity.getLevel())) {
				root.getRoutes().add(menu);
				categories.put(menuEntity.getId(), menu);
			} else if (MenuLevelEnum.PAGE.equals(menuEntity.getLevel())) {
				MenuVO parent = categories.get(menuEntity.getParentId());
				if (parent != null) {
					if (parent.getRoutes() == null) {
						parent.setRoutes(new ArrayList<>());
					}
					parent.getRoutes().add(menu);
				}
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
		AppEntity app = appMapper.selectOne(new LambdaQueryWrapper<AppEntity>()
				.eq(AppEntity::getNumber, appNumber));
		Long appId = app == null ? null : app.getId();
		if (appId == null) {
			MenuVO empty = new MenuVO();
			empty.setRoutes(new ArrayList<>());
			return empty;
		}
		return getUserMenusByAppId(userId, appId);
	}

	public MenuEntity getById(Long id) {
		return mapper.selectById(id);
	}

	public MenuDetailVO getDetail(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "菜单ID不能为空");
		}
		MenuEntity entity = mapper.selectById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "菜单不存在");
		}
		MenuDetailVO vo = toDetailVo(entity);
		// 填充父菜单信息
		if (entity.getParentId() != null && entity.getParentId() > 0) {
			MenuEntity parentEntity = mapper.selectById(entity.getParentId());
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

	@Transactional(rollbackFor = Exception.class)
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
