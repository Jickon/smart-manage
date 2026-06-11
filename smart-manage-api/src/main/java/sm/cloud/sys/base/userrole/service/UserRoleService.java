package sm.cloud.sys.base.userrole.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.userrole.domain.entity.UserRoleEntity;
import sm.cloud.sys.base.userrole.domain.form.UserRoleSaveForm;
import sm.cloud.sys.base.userrole.domain.vo.UserRoleVO;
import sm.cloud.sys.base.userrole.mapper.UserRoleMapper;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 用户角色服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserRoleService {
	private final UserRoleMapper mapper;

	/**
	 * 获取用户在指定组织下的角色列表
	 */
	public List<UserRoleVO> getUserRoles(Long userId, Long orgId) {
		return mapper.selectByUserAndOrg(userId, orgId);
	}

	/**
	 * 批量保存用户角色（先删后增）
	 */
	@Transactional(rollbackFor = Exception.class)
	public void save(UserRoleSaveForm form) {
		// 先删除用户在该组织下的所有角色
		mapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
				.eq(UserRoleEntity::getUserId, form.getUserId())
				.eq(UserRoleEntity::getOrgId, form.getOrgId()));

		// 批量插入新的角色关联
		if (form.getRoleIds() != null && !form.getRoleIds().isEmpty()) {
			List<UserRoleEntity> entities = new ArrayList<>();
			for (Long roleId : form.getRoleIds()) {
				UserRoleEntity entity = new UserRoleEntity();
				entity.setId(IdWorker.getId());
				entity.setCreateTime(LocalDateTime.now());
				entity.setCreateUser(UserHelper.getCurrentUserId());
				entity.setUserId(form.getUserId());
				entity.setOrgId(form.getOrgId());
				entity.setRoleId(roleId);
				entities.add(entity);
			}
			mapper.insertBatch(entities);
		}
	}

	/**
	 * 删除用户角色关联
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "用户角色关联ID不能为空");
		}
		UserRoleEntity entity = mapper.selectById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "用户角色关联不存在");
		}
		mapper.deleteById(id);
	}

	/**
	 * 获取用户的角色ID列表
	 */
	public List<Long> getRoleIdsByUser(Long userId, Long orgId) {
		List<UserRoleEntity> entities = mapper.selectList(new LambdaQueryWrapper<UserRoleEntity>()
				.select(UserRoleEntity::getRoleId)
				.eq(UserRoleEntity::getUserId, userId)
				.eq(UserRoleEntity::getOrgId, orgId));
		return entities.stream()
				.map(UserRoleEntity::getRoleId)
				.collect(Collectors.toList());
	}
}
