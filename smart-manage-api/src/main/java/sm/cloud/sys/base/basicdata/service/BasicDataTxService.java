package sm.cloud.sys.base.basicdata.service;

import com.alicp.jetcache.anno.CacheType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.basicdata.domain.entity.BasicDataEntity;
import sm.cloud.sys.base.basicdata.domain.form.BasicDataSaveForm;
import sm.cloud.sys.base.basicdata.mapper.BasicDataMapper;
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
 * 基础数据事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BasicDataTxService {
    private final BasicDataMapper mapper;
    private final BasicDataItemMapper itemMapper;
    private final CacheHelper cacheHelper;

    public Long save(BasicDataSaveForm form) {
        // 保存基础数据
        BasicDataEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectById(form.getId());
            if (entity == null) {
                throw new BizException(ResultEnum.NOT_FOUND, "基础数据不存在");
            }
        } else {
            entity = new BasicDataEntity();
        }
        String typeNumber = form.getId() != null ? entity.getNumber() : form.getNumber();
        entity.setNumber(typeNumber);
        entity.setName(form.getName());
        entity.setRemark(form.getRemark());
        entity.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }

        // 保存明细：删除旧项，重新插入
        itemMapper.delete(
                new LambdaQueryWrapper<BasicDataItemEntity>()
                        .eq(BasicDataItemEntity::getTypeNumber, typeNumber));
        if (form.getEntrys() != null) {
            for (BasicDataItemSaveForm itemForm : form.getEntrys()) {
                BasicDataItemEntity item = new BasicDataItemEntity();
                item.setTypeNumber(typeNumber);
                item.setItemCode(itemForm.getItemCode());
                item.setItemLabel(itemForm.getItemLabel());
                item.setSort(itemForm.getSort() != null ? itemForm.getSort() : 99);
                item.setEnableFlag(itemForm.getEnableFlag() != null ? itemForm.getEnableFlag() : true);
                itemMapper.insert(item);
            }
        }

        // 保存后清除缓存，key 对齐 @Cached 注解中的 #typeNumber
        cacheHelper.<String, List<BasicDataOptionVO>>getCache(RedisKeyConstant.CACHE_BASIC_DATA_ITEMS, CacheType.LOCAL)
                .remove(typeNumber);

        return entity.getId();
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "基础数据ID不能为空");
        }
        BasicDataEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "基础数据不存在");
        }
        mapper.deleteById(id);
    }
}
