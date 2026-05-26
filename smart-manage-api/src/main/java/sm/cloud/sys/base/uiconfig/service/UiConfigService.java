package sm.cloud.sys.base.uiconfig.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.uiconfig.domain.entity.UiConfigEntity;
import sm.cloud.sys.base.uiconfig.domain.entity.table.UiConfigTable;
import sm.cloud.sys.base.uiconfig.domain.form.UiConfigListForm;
import sm.cloud.sys.base.uiconfig.domain.form.UiConfigSaveForm;
import sm.cloud.sys.base.uiconfig.domain.vo.UiConfigDetailVO;
import sm.cloud.sys.base.uiconfig.domain.vo.UiConfigListVO;
import sm.cloud.sys.base.uiconfig.mapper.UiConfigMapper;
import sm.system.response.PageResult;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 界面配置服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UiConfigService {
    private final UiConfigMapper mapper;

    public PageResult<UiConfigListVO> listPage(UiConfigListForm form) {
        QueryWrapper qw = QueryWrapper.create().from(UiConfigTable.UI_CONFIG);
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.and(UiConfigTable.UI_CONFIG.PAGE_TITLE.like(kw)
                    .or(UiConfigTable.UI_CONFIG.SYSTEM_NAME.like(kw)));
        }
        qw.orderBy(UiConfigTable.UI_CONFIG.ID, true);
        Page<UiConfigEntity> page = Page.of(form.getPageNum(), form.getPageSize());
        Page<UiConfigEntity> result = mapper.paginate(page, qw);
        List<UiConfigListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
        return PageResult.of(result.getTotalRow(), vos);
    }

    private UiConfigListVO toListVo(UiConfigEntity entity) {
        UiConfigListVO vo = new UiConfigListVO();
        vo.setId(entity.getId());
        vo.setPageTitle(entity.getPageTitle());
        vo.setSystemName(entity.getSystemName());
        vo.setLoginBanner(entity.getLoginBanner());
        vo.setLoginLogo(entity.getLoginLogo());
        vo.setHeaderLogo(entity.getHeaderLogo());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    public UiConfigEntity getById(Long id) {
        return mapper.selectOneById(id);
    }

    public UiConfigDetailVO getDetail(Long id) {
        UiConfigEntity entity = mapper.selectOneById(id);
        return entity == null ? null : toDetailVo(entity);
    }

    /** 获取活跃配置（Caffeine 本地缓存） */
    @Cached(cacheType = CacheType.LOCAL, name = "common", key = "'ui:config'", expire = 30, timeUnit = TimeUnit.MINUTES)
    public UiConfigDetailVO getActiveConfig() {
        List<UiConfigEntity> entities = mapper.selectAll();
        return entities.isEmpty() ? new UiConfigDetailVO() : toDetailVo(entities.get(0));
    }

    private UiConfigDetailVO toDetailVo(UiConfigEntity entity) {
        UiConfigDetailVO vo = new UiConfigDetailVO();
        vo.setId(entity.getId());
        vo.setPageTitle(entity.getPageTitle());
        vo.setSystemName(entity.getSystemName());
        vo.setLoginBanner(entity.getLoginBanner());
        vo.setLoginLogo(entity.getLoginLogo());
        vo.setHeaderLogo(entity.getHeaderLogo());
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "common", key = "'ui:config'")
    public Long save(UiConfigSaveForm form) {
        UiConfigEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectOneById(form.getId());
            if (entity == null) {
                return null;
            }
        } else {
            entity = new UiConfigEntity();
        }
        entity.setPageTitle(form.getPageTitle());
        entity.setSystemName(form.getSystemName());
        entity.setLoginBanner(form.getLoginBanner());
        entity.setLoginLogo(form.getLoginLogo());
        entity.setHeaderLogo(form.getHeaderLogo());
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.update(entity);
        }
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "common", key = "'ui:config'")
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }
}
