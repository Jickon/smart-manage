package sm.domain.sys.base.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.common.helper.UserHelper;
import sm.domain.sys.base.user.mapper.UserMapper;
import sm.domain.sys.base.user.mapper.UserRoleMapper;
import sm.domain.sys.base.user.model.entity.UserEntity;
import sm.domain.sys.base.user.model.entity.UserRoleEntity;
import sm.domain.sys.base.user.model.form.UserSaveForm;
import sm.domain.sys.base.user.model.form.UserRoleAssignForm;
import sm.domain.sys.base.user.constant.UserThemeColor;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;
import sm.system.helper.Argon2Helper;

import java.util.Objects;
import java.util.List;
import sm.system.util.EnabledCommandUtil;

/**
 * 用户事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class UserTxService {
    private final UserMapper mapper;
    private final UserRoleMapper userRoleMapper;

    /** 新增/编辑用户 */
    public Long save(UserSaveForm form) {
        // 检查用户名唯一性
        LambdaQueryWrapper<UserEntity> checkWrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, form.getUsername());
        if (form.getId() != null) {
            checkWrapper.ne(UserEntity::getId, form.getId());
        }
        if (mapper.selectCount(checkWrapper) > 0) {
            throw new BizException(ResultEnum.UNIQUE_CONFLICT, "用户名已存在");
        }

        UserEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "用户不存在");
            }
            if (form.getVersion() == null) {
                throw new BizException(ResultEnum.PARAM_ERROR, "修改用户时乐观锁版本号不能为空");
            }
            if (!Objects.equals(entity.getVersion(), form.getVersion())) {
                throw new BizException(ResultEnum.DATA_CONFLICT, "用户已被其他用户修改，请刷新后重试");
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
            entity.setThemeColor(UserThemeColor.normalizeRequired(form.getThemeColor()));
        }
        if (form.getId() == null) {
            // 新增用户：密码必填
            if (entity.getPassword() == null || entity.getPassword().isBlank()) {
                throw new BizException(ResultEnum.PARAM_ERROR, "新增用户密码不能为空");
            }
            entity.setEnabled(true);
            if (mapper.insert(entity) != 1) {
                throw new BizException(sm.system.response.ResultEnum.PERSISTENCE_ERROR, "新增数据失败");
            }
        } else {
            if (mapper.updateById(entity) == 0) {
                throw new BizException(ResultEnum.DATA_CONFLICT, "用户已被其他用户修改，请刷新后重试");
            }
        }
        return entity.getId();
    }

    /** 只更新当前用户的个人主题配置，不允许借此修改其他资料。 */
    public void updateCurrentTheme(Long userId, String themeColor) {
        UserEntity entity = mapper.selectById(userId);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "用户不存在");
        }
        entity.setThemeColor(UserThemeColor.normalizeRequired(themeColor));
        if (mapper.updateById(entity) == 0) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "用户信息已变化，请刷新后重试");
        }
    }

    public void updateEnabled(List<Long> ids, boolean enabled) {
        if (!enabled && ids.contains(UserHelper.getCurrentUserId())) {
            throw new BizException(ResultEnum.BILL_STATUS_ERROR, "不能禁用当前登录用户");
        }
        EnabledCommandUtil.update(mapper, UserEntity::getId, UserEntity::getEnabled, ids, enabled, "用户");
    }

    /** 删除用户 */
    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "用户ID不能为空");
        }
        // 不能删除自己
        if (id.equals(UserHelper.getCurrentUserId())) {
            throw new BizException(ResultEnum.BILL_STATUS_ERROR, "不能删除当前登录用户");
        }
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, id));
        if (mapper.deleteById(id) == 0) {
            throw new BizException(ResultEnum.DATA_CONFLICT, "用户不存在或已被删除");
        }
    }

    /** 当前组织下的角色关系通过独立命令整体替换。 */
    public void assignRoles(UserRoleAssignForm form) {
        if (mapper.selectById(form.getUserId()) == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "用户不存在");
        }
        Long orgId = UserHelper.getCurrentOrgId();
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, form.getUserId())
                .eq(UserRoleEntity::getOrgId, orgId));
        for (Long roleId : form.getRoleIds()) {
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setUserId(form.getUserId());
            userRoleEntity.setOrgId(orgId);
            userRoleEntity.setRoleId(roleId);
            if (userRoleMapper.insert(userRoleEntity) != 1) {
                throw new BizException(sm.system.response.ResultEnum.PERSISTENCE_ERROR, "聚合明细写入失败");
            }
        }
    }
}
