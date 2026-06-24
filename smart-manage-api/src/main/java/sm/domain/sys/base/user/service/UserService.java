package sm.domain.sys.base.user.service;

import cn.dev33.satoken.stp.StpUtil;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.common.constant.UserConstant;
import sm.domain.sys.base.common.helper.UserHelper;
import sm.domain.sys.base.login.model.vo.LoginVO;
import sm.domain.sys.base.menu.service.MenuService;
import sm.domain.sys.base.permission.service.PermissionService;
import sm.domain.sys.base.user.model.entity.UserEntity;
import sm.domain.sys.base.user.model.form.UserListForm;
import sm.domain.sys.base.user.model.form.UserSaveForm;
import sm.domain.sys.base.user.model.vo.UserCreateNewDataVO;
import sm.domain.sys.base.user.model.vo.UserInfoVO;
import sm.domain.sys.base.user.model.vo.UserListVO;
import sm.domain.sys.base.user.mapper.UserMapper;
import sm.system.helper.Argon2Helper;
import sm.system.response.PageResult;
import sm.system.util.BeanUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
	private final UserMapper mapper;
	private final UserTxService txService;
	private final MenuService menuService;
	private final PermissionService permissionService;

	@Value("${smart-manage.org.default-id:1}")
	private Long defaultOrgId;

	public PageResult<UserListVO> listPage(UserListForm form) {
		LambdaQueryWrapper<UserEntity> qw = new LambdaQueryWrapper<UserEntity>().orderByAsc(UserEntity::getId);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(condition -> condition.like(UserEntity::getUsername, kw).or().like(UserEntity::getNickname, kw));
		}
		Page<UserEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<UserEntity> result = mapper.selectPage(page, qw);
		var vos = result.getRecords().stream().map(this::toUserListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), vos);
	}

	private UserListVO toUserListVo(UserEntity entity) {
		UserListVO vo = new UserListVO();
		vo.setId(entity.getId());
		vo.setUsername(entity.getUsername());
		vo.setNickname(entity.getNickname());
		vo.setAvatar(entity.getAvatar());
		return vo;
	}

	public Long save(UserSaveForm form) {
		return txService.save(form);
	}

	public void deleteById(Long id) {
		txService.deleteById(id);
	}

	public LoginVO login(String username, String password) {
		// 查询用户
		UserEntity user = mapper.selectOne(
				new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
		if (user == null) {
			return new LoginVO("用户名或密码错误");
		}

		// 使用 Argon2 验证密码
		if (!Argon2Helper.verify(user.getPassword(), password)) {
			return new LoginVO("用户名或密码错误");
		}

		// 检查用户状态
		if (user.getEnableFlag() == null || !user.getEnableFlag()) {
			return new LoginVO("用户已被禁用");
		}

		// 登录成功，使用 Sa-Token 登录
		StpUtil.login(user.getId());
		// 默认组织，供权限与业务按组织维度使用
		UserHelper.setCurrentOrgId(defaultOrgId);
		String token = StpUtil.getTokenValue();

		LoginVO vo = new LoginVO();
		vo.setToken(token);
		vo.setNickname(user.getNickname());
		vo.setAccess(UserConstant.SUPER_ADMIN.equalsIgnoreCase(user.getUsername()) ? "kdcloud" : "");
		return vo;
	}

	public UserInfoVO current() {
		// 直接走 mapper，避免自调用绕过缓存代理
		UserEntity userEntity = mapper.selectById(UserHelper.getCurrentUserId());
		return BeanUtil.copyProperties(userEntity, UserInfoVO.class);
	}

	/**
	 * 按前缀获取当前用户的权限编码列表
	 */
	public List<String> permissions(String prefix) {
		if (UserHelper.isAdmin()) {
			return List.of("*");
		}
		return permissionService.getUserPermissionsByPrefix(UserHelper.getCurrentUserId(), UserHelper.getCurrentOrgId(), prefix);
	}

	/** Redis 远程缓存读取（外部调用时走代理生效，内部调用请直接使用 mapper） */
	@Cached(cacheType = CacheType.REMOTE, name = "userInfo", key = "#id", expire = 1, timeUnit = TimeUnit.HOURS)
	public UserEntity getById(Long id) {
		return mapper.selectById(id);
	}

	/**
	 * 获取用户新增默认值
	 */
	public UserCreateNewDataVO createNewData() {
		UserCreateNewDataVO vo = new UserCreateNewDataVO();
		// 默认组织ID
		vo.setDefaultOrgId(defaultOrgId);
		// 默认启用
		vo.setEnableFlag(true);
		// 可根据业务需要设置默认角色等
		return vo;
	}
}
