package sm.cloud.sys.base.user.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.login.domain.vo.LoginVO;
import sm.cloud.sys.base.menu.service.MenuService;
import sm.cloud.sys.base.permission.service.PermissionService;
import sm.cloud.sys.base.user.domain.entity.UserEntity;
import sm.cloud.sys.base.user.domain.form.UserListForm;
import sm.cloud.sys.base.user.domain.form.UserSaveForm;
import sm.cloud.sys.base.user.domain.vo.UserCreateNewDataVO;
import sm.cloud.sys.base.user.domain.vo.UserInfoVO;
import sm.cloud.sys.base.user.domain.vo.UserListVO;
import sm.cloud.sys.base.user.mapper.UserMapper;
import sm.cloud.sys.common.constat.UserConst;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.exception.BizException;
import sm.system.helper.Argon2Helper;
import sm.system.response.PageResult;
import sm.system.util.BeanUtil;

import java.util.List;
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
	private final UserManage manage;
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

	private UserListVO toUserListVo(UserEntity e) {
		UserListVO vo = new UserListVO();
		vo.setId(e.getId());
		vo.setUsername(e.getUsername());
		vo.setNickname(e.getNickname());
		vo.setAvatar(e.getAvatar());
		return vo;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long save(UserSaveForm form) {
		// 检查用户名唯一性
		LambdaQueryWrapper<UserEntity> checkWrapper = new LambdaQueryWrapper<UserEntity>()
				.eq(UserEntity::getUsername, form.getUsername());
		if (form.getId() != null) {
			checkWrapper.ne(UserEntity::getId, form.getId());
		}
		if (mapper.selectCount(checkWrapper) > 0) {
			throw new BizException("用户名已存在");
		}

		UserEntity e;
		if (form.getId() != null) {
			e = manage.getById(form.getId());
			if (e == null) {
				throw new BizException("用户不存在");
			}
		} else {
			e = new UserEntity();
		}

		e.setUsername(form.getUsername());
		// 密码处理：新增时必填，修改时可选
		if (form.getPassword() != null && !form.getPassword().isEmpty()) {
			// 使用 Argon2 加密密码
			e.setPassword(Argon2Helper.encode(form.getPassword()));
		}
		if (form.getNickname() != null) {
			e.setNickname(form.getNickname());
		}

		if (form.getId() == null) {
			// 新增用户
			if (e.getPassword() == null || e.getPassword().isEmpty()) {
				// 默认密码 123456
				e.setPassword(Argon2Helper.encode("123456"));
			}
			e.setEnableFlag(true);
			mapper.insert(e);
		} else {
			mapper.updateById(e);
		}
		return e.getId();
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long id) {
		// 不能删除自己
		if (id.equals(UserHelper.getCurrentUserId())) {
			throw new BizException("不能删除当前登录用户");
		}
		mapper.deleteById(id);
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
		vo.setAccess(UserConst.SUPER_ADMIN.equalsIgnoreCase(user.getUsername()) ? "kdcloud" : "");
		return vo;
	}

	public UserInfoVO current() {
		UserEntity userEntity = manage.getById(UserHelper.getCurrentUserId());
		UserInfoVO userInfoVO = BeanUtil.copyProperties(userEntity, UserInfoVO.class);
		return userInfoVO;
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

	public UserEntity getById(Long id) {
		return manage.getById(id);
	}

	/**
	 * 获取用户新增默认值
	 */
	public UserCreateNewDataVO createNewData() {
		UserCreateNewDataVO form = new UserCreateNewDataVO();
		// 默认组织ID
		form.setDefaultOrgId(defaultOrgId);
		// 默认启用
		form.setEnableFlag(true);
		// 可根据业务需要设置默认角色等
		return form;
	}
}
