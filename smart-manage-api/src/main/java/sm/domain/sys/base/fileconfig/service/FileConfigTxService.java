package sm.domain.sys.base.fileconfig.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.base.fileconfig.model.entity.FileConfigEntity;
import sm.domain.sys.base.fileconfig.model.form.FileConfigSaveForm;
import sm.domain.sys.base.fileconfig.mapper.FileConfigMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 文件配置事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class FileConfigTxService {
    private final FileConfigMapper mapper;

    @CacheInvalidate(name = "common", key = "'file:config'")
    public Long save(FileConfigSaveForm form) {
        FileConfigEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "文件配置不存在");
            }
        } else {
            entity = new FileConfigEntity();
        }
        entity.setStorageType(form.getStorageType());
        entity.setLocalDir(form.getLocalDir());
        entity.setFtpHost(form.getFtpHost());
        entity.setFtpPort(form.getFtpPort());
        entity.setFtpUsername(form.getFtpUsername());
        entity.setFtpPassword(form.getFtpPassword());
        entity.setFtpDir(form.getFtpDir());
        entity.setFtpPassiveMode(form.getFtpPassiveMode());
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return entity.getId();
    }

    @CacheInvalidate(name = "common", key = "'file:config'")
    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "文件配置ID不能为空");
        }
        FileConfigEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "文件配置不存在");
        }
        mapper.deleteById(id);
    }
}
