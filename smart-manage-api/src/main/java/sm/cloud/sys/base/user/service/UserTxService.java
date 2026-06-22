package sm.cloud.sys.base.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.user.domain.entity.UserEntity;
import sm.cloud.sys.base.user.domain.form.UserSaveForm;
import sm.cloud.sys.base.user.mapper.UserMapper;
import sm.cloud.sys.common.helper.UserHelper;
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

        if (form.getId() == null) {
            // 新增用户
            if (entity.getPassword() == null || entity.getPassword().isEmpty()) {
                // 默认密码 123456
                entity.setPassword(Argon2Helper.encode("123456"));
            }
            entity.setEnableFlag(true);
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
