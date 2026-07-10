package sm.domain.sys.base.role.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.role.model.entity.RoleEntity;
import sm.domain.sys.base.role.model.form.RoleSaveForm;
import sm.domain.sys.base.role.mapper.RoleMapper;
import sm.domain.sys.base.role.mapper.RolePermissionMapper;
import sm.domain.sys.base.role.model.entity.RolePermissionEntity;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.util.Objects;

/**
 * 角色事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class RoleTxService {
    private final RoleMapper mapper;
    private final RolePermissionMapper permissionMapper;

    public Long save(RoleSaveForm form) {
        // 检查角色编码唯一性
        LambdaQueryWrapper<RoleEntity> checkWrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getNumber, form.getNumber());
        if (form.getId() != null) {
            checkWrapper.ne(RoleEntity::getId, form.getId());
        }
        if (mapper.selectCount(checkWrapper) > 0) {
            throw new BizException("角色编码已存在");
        }

        RoleEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException("角色不存在");
            }
            if (form.getMutex() == null) {
                throw new BizException(ResultEnum.PARAM_ERROR, "修改角色时乐观锁版本号不能为空");
            }
            if (!Objects.equals(entity.getMutex(), form.getMutex())) {
                throw new BizException(ResultEnum.DATA_CONFLICT, "角色已被其他用户修改，请刷新后重试");
            }
        } else {
            entity = new RoleEntity();
        }
        entity.setName(form.getName());
        entity.setNumber(form.getNumber());

        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            if (mapper.updateById(entity) == 0) {
                throw new BizException(ResultEnum.DATA_CONFLICT, "角色已被其他用户修改，请刷新后重试");
            }
        }
        replacePermissions(entity.getId(), form);
        return entity.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "角色ID不能为空");
        }
        RoleEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "角色不存在");
        }
        permissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>()
                .eq(RolePermissionEntity::getRoleId, id));
        if (mapper.deleteById(id) == 0) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "角色已被其他用户删除");
        }
    }

    /** 权限关联属于角色聚合，保存时整体替换。 */
    private void replacePermissions(Long roleId, RoleSaveForm form) {
        permissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>()
                .eq(RolePermissionEntity::getRoleId, roleId));
        for (Long permissionId : form.getPermissionIds()) {
            RolePermissionEntity permissionEntity = new RolePermissionEntity();
            permissionEntity.setRoleId(roleId);
            permissionEntity.setPermissionId(permissionId);
            permissionMapper.insert(permissionEntity);
        }
    }
}
