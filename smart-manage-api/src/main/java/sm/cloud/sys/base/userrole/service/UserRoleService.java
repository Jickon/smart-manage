package sm.cloud.sys.base.userrole.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.role.domain.entity.table.RoleTable;
import sm.cloud.sys.base.userrole.domain.entity.UserRoleEntity;
import sm.cloud.sys.base.userrole.domain.entity.table.UserRoleTable;
import sm.cloud.sys.base.userrole.domain.form.UserRoleSaveForm;
import sm.cloud.sys.base.userrole.domain.vo.UserRoleVO;
import sm.cloud.sys.base.userrole.mapper.UserRoleMapper;

import java.util.ArrayList;
import java.util.List;
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
		QueryWrapper qw = QueryWrapper.create()
				.select(
						UserRoleTable.USER_ROLE.ID,
						UserRoleTable.USER_ROLE.USER_ID,
						UserRoleTable.USER_ROLE.ORG_ID,
						UserRoleTable.USER_ROLE.ROLE_ID,
						RoleTable.ROLE.NAME.as("roleName"),
						RoleTable.ROLE.NUMBER.as("roleNumber")
				)
				.from(UserRoleTable.USER_ROLE)
				.leftJoin(RoleTable.ROLE).on(UserRoleTable.USER_ROLE.ROLE_ID.eq(RoleTable.ROLE.ID))
				.where(UserRoleTable.USER_ROLE.USER_ID.eq(userId))
				.and(UserRoleTable.USER_ROLE.ORG_ID.eq(orgId));

		List<UserRoleEntity> entities = mapper.selectListByQuery(qw);

		// 由于使用了关联查询，需要手动映射
		return mapper.selectListByQueryAs(qw, UserRoleVO.class);
	}

	/**
	 * 批量保存用户角色（先删后增）
	 */
	@Transactional(rollbackFor = Exception.class)
	public void save(UserRoleSaveForm form) {
		// 先删除用户在该组织下的所有角色
		QueryWrapper deleteWrapper = QueryWrapper.create()
				.where(UserRoleTable.USER_ROLE.USER_ID.eq(form.getUserId()))
				.and(UserRoleTable.USER_ROLE.ORG_ID.eq(form.getOrgId()));
		mapper.deleteByQuery(deleteWrapper);

		// 批量插入新的角色关联
		if (form.getRoleIds() != null && !form.getRoleIds().isEmpty()) {
			List<UserRoleEntity> entities = new ArrayList<>();
			for (Long roleId : form.getRoleIds()) {
				UserRoleEntity entity = new UserRoleEntity();
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
		mapper.deleteById(id);
	}

	/**
	 * 获取用户的角色ID列表
	 */
	public List<Long> getRoleIdsByUser(Long userId, Long orgId) {
		QueryWrapper qw = QueryWrapper.create()
				.select(UserRoleTable.USER_ROLE.ROLE_ID)
				.where(UserRoleTable.USER_ROLE.USER_ID.eq(userId))
				.and(UserRoleTable.USER_ROLE.ORG_ID.eq(orgId));
		List<UserRoleEntity> entities = mapper.selectListByQuery(qw);
		return entities.stream()
				.map(UserRoleEntity::getRoleId)
				.collect(Collectors.toList());
	}
}
