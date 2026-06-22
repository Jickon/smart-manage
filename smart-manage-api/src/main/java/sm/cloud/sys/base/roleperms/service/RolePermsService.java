package sm.cloud.sys.base.roleperms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.base.roleperms.domain.entity.RolePermsEntity;
import sm.cloud.sys.base.roleperms.domain.form.RolePermsSaveForm;
import sm.cloud.sys.base.roleperms.domain.vo.RolePermsVO;
import sm.cloud.sys.base.roleperms.mapper.RolePermsMapper;

import java.util.List;

/**
 * 角色权限服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RolePermsService {
	private final RolePermsMapper mapper;
	private final RolePermsTxService txService;

	/**
	 * 获取角色的权限列表
	 */
	public List<RolePermsVO> getRolePermissions(Long roleId) {
		return mapper.selectByRoleId(roleId);
	}

	/**
	 * 批量保存角色权限，委托事务服务处理
	 */
	public void save(RolePermsSaveForm form) {
		txService.save(form);
	}

	/**
	 * 删除角色权限关联，委托事务服务处理
	 */
	public void deleteById(Long id) {
		txService.deleteById(id);
	}

	/**
	 * 获取角色的权限ID列表
	 */
	public List<Long> getPermissionIdsByRole(Long roleId) {
		List<RolePermsEntity> entityList = mapper.selectList(new LambdaQueryWrapper<RolePermsEntity>()
				.select(RolePermsEntity::getPermissionId)
				.eq(RolePermsEntity::getRoleId, roleId));
		return entityList.stream()
				.map(RolePermsEntity::getPermissionId)
				.toList();
	}
}
