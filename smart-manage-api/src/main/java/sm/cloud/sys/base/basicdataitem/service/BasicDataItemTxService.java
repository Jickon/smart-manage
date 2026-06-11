package sm.cloud.sys.base.basicdataitem.service;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.basicdataitem.domain.entity.BasicDataItemEntity;
import sm.cloud.sys.base.basicdataitem.domain.form.BasicDataItemSaveForm;
import sm.cloud.sys.base.basicdataitem.domain.vo.BasicDataOptionVO;
import sm.cloud.sys.base.basicdataitem.mapper.BasicDataItemMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.util.List;

/**
 * 基础数据项事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BasicDataItemTxService {
    private final BasicDataItemMapper mapper;

    // 缓存 key 前缀
    private static final String CACHE_PREFIX = "basic-data:items:";

    // 编程方式获取缓存实例（用于 key 依赖方法内部加载数据的场景）
    @CreateCache(name = "basic-data-items", cacheType = CacheType.LOCAL)
    private Cache<String, List<BasicDataOptionVO>> cache;

    @CacheInvalidate(name = "basic-data-items", key = "#form.typeNumber")
    public Long save(BasicDataItemSaveForm form) {
        BasicDataItemEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
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
            mapper.updateById(entity);
        }
        return entity.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "基础数据项ID不能为空");
        }
        BasicDataItemEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "基础数据项不存在");
        }
        mapper.deleteById(id);
        // 缓存 key 依赖方法内部加载的数据，使用编程方式清除
        cache.remove(CACHE_PREFIX + entity.getTypeNumber());
    }
}
