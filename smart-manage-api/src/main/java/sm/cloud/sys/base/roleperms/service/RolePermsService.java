package sm.cloud.sys.base.roleperms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.roleperms.domain.entity.RolePermsEntity;
import sm.cloud.sys.base.roleperms.domain.form.RolePermsSaveForm;
import sm.cloud.sys.base.roleperms.domain.vo.RolePermsVO;
import sm.cloud.sys.base.roleperms.mapper.RolePermsMapper;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.util.ArrayList;
import java.time.LocalDateTime;
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
		return mapper.selectByRoleId(roleId);
	}

	/**
	 * 批量保存角色权限（先删后增）
	 */
	@Transactional(rollbackFor = Exception.class)
	public void save(RolePermsSaveForm form) {
		// 先删除角色的所有权限
		mapper.delete(new LambdaQueryWrapper<RolePermsEntity>()
				.eq(RolePermsEntity::getRoleId, form.getRoleId()));

		// 批量插入新的权限关联
		if (form.getPermissionIds() != null && !form.getPermissionIds().isEmpty()) {
			List<RolePermsEntity> entities = new ArrayList<>();
			for (Long permId : form.getPermissionIds()) {
				RolePermsEntity entity = new RolePermsEntity();
				entity.setId(IdWorker.getId());
				entity.setCreateTime(LocalDateTime.now());
				entity.setCreateUser(UserHelper.getCurrentUserId());
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
		RolePermsEntity entity = mapper.selectById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "角色权限关联不存在");
		}
		mapper.deleteById(id);
	}

	/**
	 * 获取角色的权限ID列表
	 */
	public List<Long> getPermissionIdsByRole(Long roleId) {
		List<RolePermsEntity> entities = mapper.selectList(new LambdaQueryWrapper<RolePermsEntity>()
				.select(RolePermsEntity::getPermissionId)
				.eq(RolePermsEntity::getRoleId, roleId));
		return entities.stream()
				.map(RolePermsEntity::getPermissionId)
				.toList();
	}
}
