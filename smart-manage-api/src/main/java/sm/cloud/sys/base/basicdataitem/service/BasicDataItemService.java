package sm.cloud.sys.base.basicdataitem.service;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.base.basicdataitem.domain.entity.BasicDataItemEntity;
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
    private final BasicDataItemTxService txService;

    /**
     * 按基础数据编码查询项列表（管理端用）
     */
    public List<BasicDataItemListVO> listByTypeNumber(String typeNumber) {
        LambdaQueryWrapper<BasicDataItemEntity> qw = new LambdaQueryWrapper<BasicDataItemEntity>()
                .eq(BasicDataItemEntity::getTypeNumber, typeNumber)
                .orderByAsc(BasicDataItemEntity::getSort);
        List<BasicDataItemEntity> entities = mapper.selectList(qw);
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
        LambdaQueryWrapper<BasicDataItemEntity> qw = new LambdaQueryWrapper<BasicDataItemEntity>()
                .eq(BasicDataItemEntity::getTypeNumber, typeNumber)
                .eq(BasicDataItemEntity::getEnableFlag, true)
                .orderByAsc(BasicDataItemEntity::getSort);
        List<BasicDataItemEntity> entities = mapper.selectList(qw);
        return entities.stream()
                .map(e -> new BasicDataOptionVO(e.getItemCode(), e.getItemLabel()))
                .collect(Collectors.toList());
    }

    public Long save(BasicDataItemSaveForm form) {
        return txService.save(form);
    }

    public void deleteById(Long id) {
        txService.deleteById(id);
    }
}
