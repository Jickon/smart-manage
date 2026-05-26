package sm.cloud.sys.base.fileconfig.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.fileconfig.domain.entity.FileConfigEntity;
import sm.cloud.sys.base.fileconfig.domain.entity.table.FileConfigTable;
import sm.cloud.sys.base.fileconfig.domain.form.FileConfigListForm;
import sm.cloud.sys.base.fileconfig.domain.form.FileConfigSaveForm;
import sm.cloud.sys.base.fileconfig.domain.vo.FileConfigDetailVO;
import sm.cloud.sys.base.fileconfig.mapper.FileConfigMapper;
import sm.system.response.PageResult;

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
        QueryWrapper qw = QueryWrapper.create().from(FileConfigTable.FILE_CONFIG);
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.and(FileConfigTable.FILE_CONFIG.STORAGE_TYPE.like(kw));
        }
        qw.orderBy(FileConfigTable.FILE_CONFIG.ID, true);
        Page<FileConfigEntity> page = Page.of(form.getPageNum(), form.getPageSize());
        Page<FileConfigEntity> result = mapper.paginate(page, qw);
        List<FileConfigDetailVO> vos = result.getRecords().stream().map(this::toDetailVo).collect(Collectors.toList());
        return PageResult.of(result.getTotalRow(), vos);
    }

    public FileConfigEntity getById(Long id) {
        return mapper.selectOneById(id);
    }

    public FileConfigDetailVO getDetail(Long id) {
        FileConfigEntity entity = mapper.selectOneById(id);
        return entity == null ? null : toDetailVo(entity);
    }

    /** 获取活跃配置（Caffeine 本地缓存） */
    @Cached(cacheType = CacheType.LOCAL, name = "common", key = "'file:config'", expire = 30, timeUnit = TimeUnit.MINUTES)
    public FileConfigDetailVO getActiveConfig() {
        List<FileConfigEntity> entities = mapper.selectAll();
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
            entity = mapper.selectOneById(form.getId());
            if (entity == null) {
                return null;
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
            mapper.update(entity);
        }
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "common", key = "'file:config'")
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }
}
