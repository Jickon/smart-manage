package sm.cloud.sys.base.basicdataitem.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
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
import sm.cloud.sys.base.common.constant.RedisKeyConstant;
import sm.system.helper.CacheHelper;
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
    private final CacheHelper cacheHelper;

    @CacheInvalidate(name = RedisKeyConstant.CACHE_BASIC_DATA_ITEMS, key = "#form.typeNumber")
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
        // 删除后清除缓存，key 对齐 @Cached 注解中的 #typeNumber
        cacheHelper.<String, List<BasicDataOptionVO>>getCache(RedisKeyConstant.CACHE_BASIC_DATA_ITEMS, CacheType.LOCAL)
                .remove(entity.getTypeNumber());
    }
}
