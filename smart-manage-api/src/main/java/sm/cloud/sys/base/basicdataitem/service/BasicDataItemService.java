package sm.cloud.sys.base.basicdataitem.service;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CreateCache;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.basicdataitem.domain.entity.BasicDataItemEntity;
import sm.cloud.sys.base.basicdataitem.domain.entity.table.BasicDataItemTable;
import sm.cloud.sys.base.basicdataitem.domain.form.BasicDataItemSaveForm;
import sm.cloud.sys.base.basicdataitem.domain.vo.BasicDataItemListVO;
import sm.cloud.sys.base.basicdataitem.domain.vo.BasicDataOptionVO;
import sm.cloud.sys.base.basicdataitem.mapper.BasicDataItemMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基础数据项服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BasicDataItemService {
    private final BasicDataItemMapper mapper;

    // 缓存 key 前缀
    private static final String CACHE_PREFIX = "basic-data:items:";

    // 编程方式获取缓存实例（用于 key 依赖方法内部加载数据的场景）
    @CreateCache(name = "basic-data-items", cacheType = CacheType.LOCAL)
    private Cache<String, List<BasicDataOptionVO>> cache;

    /**
     * 按基础数据编码查询项列表（管理端用）
     */
    public List<BasicDataItemListVO> listByTypeNumber(String typeNumber) {
        QueryWrapper qw = QueryWrapper.create()
                .from(BasicDataItemTable.BASIC_DATA_ITEM)
                .where(BasicDataItemTable.BASIC_DATA_ITEM.TYPE_NUMBER.eq(typeNumber))
                .orderBy(BasicDataItemTable.BASIC_DATA_ITEM.SORT, true);
        List<BasicDataItemEntity> entities = mapper.selectListByQuery(qw);
        return entities.stream().map(this::toListVo).collect(Collectors.toList());
    }

    private BasicDataItemListVO toListVo(BasicDataItemEntity entity) {
        BasicDataItemListVO vo = new BasicDataItemListVO();
        vo.setId(entity.getId());
        vo.setTypeNumber(entity.getTypeNumber());
        vo.setItemCode(entity.getItemCode());
        vo.setItemLabel(entity.getItemLabel());
        vo.setSort(entity.getSort());
        vo.setEnableFlag(entity.getEnableFlag());
        return vo;
    }

    /** 按基础数据编码获取选项列表（Caffeine 本地缓存） */
    @Cached(cacheType = CacheType.LOCAL, name = "basic-data-items", key = "#typeNumber", expire = 30, timeUnit = TimeUnit.MINUTES)
    public List<BasicDataOptionVO> getOptionsByTypeNumber(String typeNumber) {
        QueryWrapper qw = QueryWrapper.create()
                .from(BasicDataItemTable.BASIC_DATA_ITEM)
                .where(BasicDataItemTable.BASIC_DATA_ITEM.TYPE_NUMBER.eq(typeNumber))
                .and(BasicDataItemTable.BASIC_DATA_ITEM.ENABLE_FLAG.eq(true))
                .orderBy(BasicDataItemTable.BASIC_DATA_ITEM.SORT, true);
        List<BasicDataItemEntity> entities = mapper.selectListByQuery(qw);
        return entities.stream()
                .map(e -> new BasicDataOptionVO(e.getItemCode(), e.getItemLabel()))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "basic-data-items", key = "#form.typeNumber")
    public Long save(BasicDataItemSaveForm form) {
        BasicDataItemEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectOneById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "基础数据项不存在");
            }
        } else {
            entity = new BasicDataItemEntity();
        }
        entity.setTypeNumber(form.getTypeNumber());
        entity.setItemCode(form.getItemCode());
        entity.setItemLabel(form.getItemLabel());
        entity.setSort(form.getSort() != null ? form.getSort() : 99);
        entity.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.update(entity);
        }
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "基础数据项ID不能为空");
        }
        BasicDataItemEntity entity = mapper.selectOneById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "基础数据项不存在");
        }
        mapper.deleteById(id);
        // 缓存 key 依赖方法内部加载的数据，使用编程方式清除
        cache.remove(CACHE_PREFIX + entity.getTypeNumber());
    }
}
