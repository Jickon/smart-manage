package sm.domain.sys.base.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.common.helper.UserHelper;
import sm.domain.sys.base.user.mapper.UserMapper;
import sm.domain.sys.base.user.model.entity.UserEntity;
import sm.domain.sys.base.user.model.form.UserSaveForm;
import sm.domain.sys.base.user.model.form.UserSaveWithRolesForm;
import sm.domain.sys.base.userrole.model.form.UserRoleSaveForm;
import sm.domain.sys.base.userrole.service.UserRoleTxService;
import sm.system.exception.BizException;
import sm.system.helper.Argon2Helper;

/**
 * 用户事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserTxService {
    private final UserMapper mapper;
    private final UserRoleTxService userRoleTxService;

    /**
     * 聚合保存：用户 + 角色分配，在同一事务内完成。
     * orgId 取自当前登录用户的 org 上下文。
     */
    public Long saveWithRoles(UserSaveWithRolesForm form) {
        // 1. 保存用户
        Long userId = save(form);
        // 2. 保存用户角色
        UserRoleSaveForm roleForm = new UserRoleSaveForm();
        roleForm.setUserId(userId);
        roleForm.setOrgId(UserHelper.getCurrentOrgId());
        roleForm.setRoleIds(form.getRoleIds());
        userRoleTxService.save(roleForm);
        return userId;
    }

    /** 新增/编辑用户 */
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

        UserEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException("用户不存在");
            }
        } else {
            entity = new UserEntity();
        }

        entity.setUsername(form.getUsername());
        // 密码处理：新增时必填，修改时可选
        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            // 使用 Argon2 加密密码
            entity.setPassword(Argon2Helper.encode(form.getPassword()));
        }
        if (form.getNickname() != null) {
            entity.setNickname(form.getNickname());
        }
        if (form.getEmail() != null) {
            entity.setEmail(form.getEmail());
        }
        if (form.getPhone() != null) {
            entity.setPhone(form.getPhone());
        }
        if (form.getAvatar() != null) {
            entity.setAvatar(form.getAvatar());
        }
        if (form.getThemeColor() != null) {
            entity.setThemeColor(form.getThemeColor());
        }
        if (form.getEnableFlag() != null) {
            entity.setEnableFlag(form.getEnableFlag());
        }

        if (form.getId() == null) {
            // 新增用户：密码必填
            if (entity.getPassword() == null || entity.getPassword().isBlank()) {
                throw new BizException("新增用户密码不能为空");
            }
            if (form.getEnableFlag() == null) {
                entity.setEnableFlag(true);
            }
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return entity.getId();
    }

    /** 删除用户 */
    public void deleteById(Long id) {
        // 不能删除自己
        if (id.equals(UserHelper.getCurrentUserId())) {
            throw new BizException("不能删除当前登录用户");
        }
        mapper.deleteById(id);
    }
}
