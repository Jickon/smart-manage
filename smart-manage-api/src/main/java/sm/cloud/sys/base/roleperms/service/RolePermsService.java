package sm.cloud.sys.base.roleperms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.permission.domain.entity.table.PermissionTable;
import sm.cloud.sys.base.roleperms.domain.entity.RolePermsEntity;
import sm.cloud.sys.base.roleperms.domain.entity.table.RolePermsTable;
import sm.cloud.sys.base.roleperms.domain.form.RolePermsSaveForm;
import sm.cloud.sys.base.roleperms.domain.vo.RolePermsVO;
import sm.cloud.sys.base.roleperms.mapper.RolePermsMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.util.ArrayList;
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

	/**
	 * 获取角色的权限列表
	 */
	public List<RolePermsVO> getRolePermissions(Long roleId) {
		QueryWrapper qw = QueryWrapper.create()
				.select(
						RolePermsTable.ROLE_PERMS.ID,
						RolePermsTable.ROLE_PERMS.ROLE_ID,
						RolePermsTable.ROLE_PERMS.PERMISSION_ID,
						PermissionTable.PERMISSION.NAME.as("permName"),
						PermissionTable.PERMISSION.NUMBER.as("permNumber")
				)
				.from(RolePermsTable.ROLE_PERMS)
				.leftJoin(PermissionTable.PERMISSION).on(RolePermsTable.ROLE_PERMS.PERMISSION_ID.eq(PermissionTable.PERMISSION.ID))
				.where(RolePermsTable.ROLE_PERMS.ROLE_ID.eq(roleId));

		return mapper.selectListByQueryAs(qw, RolePermsVO.class);
	}

	/**
	 * 批量保存角色权限（先删后增）
	 */
	@Transactional(rollbackFor = Exception.class)
	public void save(RolePermsSaveForm form) {
		// 先删除角色的所有权限
		QueryWrapper deleteWrapper = QueryWrapper.create()
				.where(RolePermsTable.ROLE_PERMS.ROLE_ID.eq(form.getRoleId()));
		mapper.deleteByQuery(deleteWrapper);

		// 批量插入新的权限关联
		if (form.getPermissionIds() != null && !form.getPermissionIds().isEmpty()) {
			List<RolePermsEntity> entities = new ArrayList<>();
			for (Long permId : form.getPermissionIds()) {
				RolePermsEntity entity = new RolePermsEntity();
				entity.setRoleId(form.getRoleId());
				entity.setPermissionId(permId);
				entities.add(entity);
			}
			mapper.insertBatch(entities);
		}
	}

	/**
	 * 删除角色权限关联
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "角色权限关联ID不能为空");
		}
		RolePermsEntity entity = mapper.selectOneById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "角色权限关联不存在");
		}
		mapper.deleteById(id);
	}

	/**
	 * 获取角色的权限ID列表
	 */
	public List<Long> getPermissionIdsByRole(Long roleId) {
		QueryWrapper qw = QueryWrapper.create()
				.select(RolePermsTable.ROLE_PERMS.PERMISSION_ID)
				.where(RolePermsTable.ROLE_PERMS.ROLE_ID.eq(roleId));
		List<RolePermsEntity> entities = mapper.selectListByQuery(qw);
		return entities.stream()
				.map(RolePermsEntity::getPermissionId)
				.toList();
	}
}
