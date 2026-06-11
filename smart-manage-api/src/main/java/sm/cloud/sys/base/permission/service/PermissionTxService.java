package sm.cloud.sys.base.permission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.permission.domain.entity.PermissionEntity;
import sm.cloud.sys.base.permission.domain.form.PermissionSaveForm;
import sm.cloud.sys.base.permission.mapper.PermissionMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 权限事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PermissionTxService {
    private final PermissionMapper mapper;

    public Long save(PermissionSaveForm form) {
        PermissionEntity e;
        if (form.getId() != null) {
            e = mapper.selectById(form.getId());
            if (e == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "权限不存在");
            }
        } else {
            e = new PermissionEntity();
        }
        e.setName(form.getName());
        e.setNumber(form.getNumber());
        e.setAppId(form.getAppId());
        if (form.getId() == null) {
            mapper.insert(e);
        } else {
            mapper.updateById(e);
        }
        return e.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "权限ID不能为空");
        }
        PermissionEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "权限不存在");
        }
        mapper.deleteById(id);
    }
}
