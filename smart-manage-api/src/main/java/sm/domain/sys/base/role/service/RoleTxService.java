package sm.domain.sys.base.role.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.role.model.entity.RoleEntity;
import sm.domain.sys.base.role.model.form.RoleSaveForm;
import sm.domain.sys.base.role.model.form.RoleSaveWithPermsForm;
import sm.domain.sys.base.role.mapper.RoleMapper;
import sm.domain.sys.base.roleperms.model.form.RolePermsSaveForm;
import sm.domain.sys.base.roleperms.service.RolePermsTxService;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 角色事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class RoleTxService {
    private final RoleMapper mapper;
    private final RolePermsTxService rolePermsTxService;

    /**
     * 聚合保存：角色 + 权限分配，在同一事务内完成。
     */
    public Long saveWithPerms(RoleSaveWithPermsForm form) {
        // 1. 保存角色
        Long roleId = save(form);
        // 2. 保存角色权限（先删后增）
        RolePermsSaveForm permsForm = new RolePermsSaveForm();
        permsForm.setRoleId(roleId);
        permsForm.setPermissionIds(form.getPermissionIds());
        rolePermsTxService.save(permsForm);
        return roleId;
    }

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
        } else {
            entity = new RoleEntity();
        }
        entity.setName(form.getName());
        entity.setNumber(form.getNumber());

        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
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
        mapper.deleteById(id);
    }
}
