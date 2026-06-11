package sm.cloud.sys.base.role.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.role.domain.entity.RoleEntity;
import sm.cloud.sys.base.role.domain.form.RoleSaveForm;
import sm.cloud.sys.base.role.mapper.RoleMapper;
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

        RoleEntity e;
        if (form.getId() != null) {
            e = mapper.selectById(form.getId());
            if (e == null) {
                throw new BizException("角色不存在");
            }
        } else {
            e = new RoleEntity();
        }
        e.setName(form.getName());
        e.setNumber(form.getNumber());

        if (form.getId() == null) {
            mapper.insert(e);
        } else {
            mapper.updateById(e);
        }
        return e.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "角色ID不能为空");
        }
        RoleEntity role = mapper.selectById(id);
        if (role == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "角色不存在");
        }
        mapper.deleteById(id);
    }
}
