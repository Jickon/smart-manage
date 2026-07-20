package sm.domain.sys.base.uiconfig.service;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.uiconfig.model.entity.UiConfigEntity;
import sm.domain.sys.base.uiconfig.model.form.UiConfigListForm;
import sm.domain.sys.base.uiconfig.model.form.UiConfigSaveForm;
import sm.domain.sys.base.uiconfig.model.vo.UiConfigDetailVO;
import sm.domain.sys.base.uiconfig.model.vo.UiConfigListVO;
import sm.domain.sys.base.uiconfig.mapper.UiConfigMapper;
import sm.system.exception.BizException;
import sm.system.aop.log.BizLog;
import sm.system.response.PageData;
import sm.system.response.ResultEnum;

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
    private final UiConfigTxService txService;

    public PageData<UiConfigListVO> listPage(UiConfigListForm form) {
        LambdaQueryWrapper<UiConfigEntity> qw = new LambdaQueryWrapper<UiConfigEntity>();
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.and(condition -> condition.like(UiConfigEntity::getPageTitle, kw).or().like(UiConfigEntity::getSystemName, kw));
        }
        qw.orderByAsc(UiConfigEntity::getId);
        Page<UiConfigEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
        Page<UiConfigEntity> result = mapper.selectPage(page, qw);
        List<UiConfigListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
        return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), vos);
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
        return mapper.selectById(id);
    }

    public UiConfigDetailVO getDetail(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "界面配置ID不能为空");
        }
        UiConfigEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "界面配置不存在");
        }
        return toDetailVo(entity);
    }

    /** 获取活跃配置（Caffeine 本地缓存） */
    @Cached(cacheType = CacheType.LOCAL, name = "common", key = "'ui:config'", expire = 30, timeUnit = TimeUnit.MINUTES)
    public UiConfigDetailVO getActiveConfig() {
        List<UiConfigEntity> entityList = mapper.selectList(null);
        return entityList.isEmpty() ? new UiConfigDetailVO() : toDetailVo(entityList.get(0));
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

    @BizLog("保存界面配置")
    @CacheInvalidate(name = "common", key = "'ui:config'")
    public Long save(UiConfigSaveForm form) {
        return txService.save(form);
    }

    @BizLog("删除界面配置")
    @CacheInvalidate(name = "common", key = "'ui:config'")
    public void deleteById(Long id) {
        txService.deleteById(id);
    }
}
