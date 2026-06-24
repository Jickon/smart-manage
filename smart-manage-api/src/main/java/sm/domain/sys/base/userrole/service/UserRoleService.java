package sm.domain.sys.base.userrole.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.userrole.model.entity.UserRoleEntity;
import sm.domain.sys.base.userrole.model.form.UserRoleSaveForm;
import sm.domain.sys.base.userrole.model.vo.UserRoleVO;
import sm.domain.sys.base.userrole.mapper.UserRoleMapper;

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
	private final UserRoleTxService txService;

	/**
	 * 获取用户在指定组织下的角色列表
	 */
	public List<UserRoleVO> getUserRoles(Long userId, Long orgId) {
		return mapper.selectByUserAndOrg(userId, orgId);
	}

	/**
	 * 批量保存用户角色，委托事务服务处理
	 */
	public void save(UserRoleSaveForm form) {
		txService.save(form);
	}

	/**
	 * 删除用户角色关联，委托事务服务处理
	 */
	public void deleteById(Long id) {
		txService.deleteById(id);
	}

	/**
	 * 获取用户的角色ID列表
	 */
	public List<Long> getRoleIdsByUser(Long userId, Long orgId) {
		List<UserRoleEntity> entityList = mapper.selectList(new LambdaQueryWrapper<UserRoleEntity>()
				.select(UserRoleEntity::getRoleId)
				.eq(UserRoleEntity::getUserId, userId)
				.eq(UserRoleEntity::getOrgId, orgId));
		return entityList.stream()
				.map(UserRoleEntity::getRoleId)
				.collect(Collectors.toList());
	}
}
