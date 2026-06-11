package sm.cloud.sys.base.fileconfig.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.fileconfig.domain.entity.FileConfigEntity;
import sm.cloud.sys.base.fileconfig.domain.form.FileConfigListForm;
import sm.cloud.sys.base.fileconfig.domain.form.FileConfigSaveForm;
import sm.cloud.sys.base.fileconfig.domain.vo.FileConfigDetailVO;
import sm.cloud.sys.base.fileconfig.mapper.FileConfigMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文件配置服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileConfigService {
    private final FileConfigMapper mapper;

    public PageResult<FileConfigDetailVO> listPage(FileConfigListForm form) {
        LambdaQueryWrapper<FileConfigEntity> qw = new LambdaQueryWrapper<FileConfigEntity>();
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.like(FileConfigEntity::getStorageType, kw);
        }
        qw.orderByAsc(FileConfigEntity::getId);
        Page<FileConfigEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
        Page<FileConfigEntity> result = mapper.selectPage(page, qw);
        List<FileConfigDetailVO> vos = result.getRecords().stream().map(this::toDetailVo).collect(Collectors.toList());
        return PageResult.of(result.getTotal(), vos);
    }

    public FileConfigEntity getById(Long id) {
        return mapper.selectById(id);
    }

    public FileConfigDetailVO getDetail(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "文件配置ID不能为空");
        }
        FileConfigEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "文件配置不存在");
        }
        return toDetailVo(entity);
    }

    /** 获取活跃配置（Caffeine 本地缓存） */
    @Cached(cacheType = CacheType.LOCAL, name = "common", key = "'file:config'", expire = 30, timeUnit = TimeUnit.MINUTES)
    public FileConfigDetailVO getActiveConfig() {
        List<FileConfigEntity> entities = mapper.selectList(null);
        return entities.isEmpty() ? defaultConfig() : toDetailVo(entities.get(0));
    }

    private FileConfigDetailVO defaultConfig() {
        FileConfigDetailVO vo = new FileConfigDetailVO();
        vo.setStorageType("LOCAL");
        vo.setLocalDir("E:/upload/");
        return vo;
    }

    private FileConfigDetailVO toDetailVo(FileConfigEntity entity) {
        FileConfigDetailVO vo = new FileConfigDetailVO();
        vo.setId(entity.getId());
        vo.setStorageType(entity.getStorageType());
        vo.setLocalDir(entity.getLocalDir());
        vo.setFtpHost(entity.getFtpHost());
        vo.setFtpPort(entity.getFtpPort());
        vo.setFtpUsername(entity.getFtpUsername());
        vo.setFtpPassword(entity.getFtpPassword());
        vo.setFtpDir(entity.getFtpDir());
        vo.setFtpPassiveMode(entity.getFtpPassiveMode());
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
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

    @Transactional(rollbackFor = Exception.class)
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
