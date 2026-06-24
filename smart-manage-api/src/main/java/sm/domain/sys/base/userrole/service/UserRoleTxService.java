package sm.domain.sys.base.userrole.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.common.helper.UserHelper;
import sm.domain.sys.base.userrole.model.entity.UserRoleEntity;
import sm.domain.sys.base.userrole.model.form.UserRoleSaveForm;
import sm.domain.sys.base.userrole.mapper.UserRoleMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserRoleTxService {
    private final UserRoleMapper mapper;

    /** 批量保存用户角色（先删后增） */
    public void save(UserRoleSaveForm form) {
        // 先删除用户在该组织下的所有角色
        mapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, form.getUserId())
                .eq(UserRoleEntity::getOrgId, form.getOrgId()));

        // 批量插入新的角色关联
        if (form.getRoleIds() != null && !form.getRoleIds().isEmpty()) {
            List<UserRoleEntity> entityList = new ArrayList<>();
            for (Long roleId : form.getRoleIds()) {
                UserRoleEntity entity = new UserRoleEntity();
                entity.setId(IdWorker.getId());
                entity.setCreateTime(LocalDateTime.now());
                entity.setCreateUser(UserHelper.getCurrentUserId());
                entity.setUserId(form.getUserId());
                entity.setOrgId(form.getOrgId());
                entity.setRoleId(roleId);
                entityList.add(entity);
            }
            mapper.insertBatch(entityList);
        }
    }

    /** 删除用户角色关联 */
    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "用户角色关联ID不能为空");
        }
        UserRoleEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "用户角色关联不存在");
        }
        mapper.deleteById(id);
    }
}
