package sm.cloud.sys.base.basicdata.service;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.mybatisflex.core.paginate.Page;
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
import sm.cloud.sys.base.basicdata.domain.entity.BasicDataEntity;
import sm.cloud.sys.base.basicdata.domain.entity.table.BasicDataTable;
import sm.cloud.sys.base.basicdata.domain.form.BasicDataListForm;
import sm.cloud.sys.base.basicdata.domain.form.BasicDataSaveForm;
import sm.cloud.sys.base.basicdata.domain.vo.BasicDataCreateNewDataVO;
import sm.cloud.sys.base.basicdata.domain.vo.BasicDataDetailVO;
import sm.cloud.sys.base.basicdata.domain.vo.BasicDataListVO;
import sm.cloud.sys.base.basicdata.mapper.BasicDataMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础数据服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BasicDataService {
    private final BasicDataMapper mapper;
    private final BasicDataItemMapper itemMapper;

    private static final String CACHE_PREFIX = "basic-data:items:";

    // 编程方式获取缓存实例（用于 key 依赖方法内部计算逻辑的场景）
    @CreateCache(name = "basic-data-items", cacheType = CacheType.LOCAL)
    private Cache<String, List<BasicDataOptionVO>> cache;

    public PageResult<BasicDataListVO> listPage(BasicDataListForm form) {
        QueryWrapper qw = QueryWrapper.create().from(BasicDataTable.BASIC_DATA);
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.and(BasicDataTable.BASIC_DATA.NAME.like(kw).or(BasicDataTable.BASIC_DATA.NUMBER.like(kw)));
        }
        qw.orderBy(BasicDataTable.BASIC_DATA.NUMBER, true);
        Page<BasicDataEntity> page = Page.of(form.getPageNum(), form.getPageSize());
        Page<BasicDataEntity> result = mapper.paginate(page, qw);
        List<BasicDataListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
        return PageResult.of(result.getTotalRow(), vos);
    }

    private BasicDataListVO toListVo(BasicDataEntity entity) {
        BasicDataListVO vo = new BasicDataListVO();
        vo.setId(entity.getId());
        vo.setNumber(entity.getNumber());
        vo.setName(entity.getName());
        vo.setRemark(entity.getRemark());
        vo.setEnableFlag(entity.getEnableFlag());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /** 详情（含明细 entrys，一次请求） */
    public BasicDataDetailVO getById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "基础数据ID不能为空");
        }
        BasicDataEntity entity = mapper.selectOneById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "基础数据不存在");
        }
        BasicDataDetailVO vo = new BasicDataDetailVO();
        vo.setId(entity.getId());
        vo.setNumber(entity.getNumber());
        vo.setName(entity.getName());
        vo.setRemark(entity.getRemark());
        vo.setEnableFlag(entity.getEnableFlag());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        // 加载明细
        List<BasicDataItemEntity> items = itemMapper.selectListByQuery(
                QueryWrapper.create().from(BasicDataItemTable.BASIC_DATA_ITEM)
                        .where(BasicDataItemTable.BASIC_DATA_ITEM.TYPE_NUMBER.eq(entity.getNumber()))
                        .orderBy(BasicDataItemTable.BASIC_DATA_ITEM.SORT, true));
        vo.setEntrys(items.stream().map(item -> {
            BasicDataItemListVO iv = new BasicDataItemListVO();
            iv.setId(item.getId());
            iv.setTypeNumber(item.getTypeNumber());
            iv.setItemCode(item.getItemCode());
            iv.setItemLabel(item.getItemLabel());
            iv.setSort(item.getSort());
            iv.setEnableFlag(item.getEnableFlag());
            return iv;
        }).collect(Collectors.toList()));
        return vo;
    }

    public BasicDataCreateNewDataVO createNewData() {
        BasicDataCreateNewDataVO vo = new BasicDataCreateNewDataVO();
        vo.setEnableFlag(true);
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long save(BasicDataSaveForm form) {
        // 保存基础数据
        BasicDataEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectOneById(form.getId());
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
            mapper.update(entity);
        }

        // 保存明细：删除旧项，重新插入
        itemMapper.deleteByQuery(
                QueryWrapper.create().from(BasicDataItemTable.BASIC_DATA_ITEM)
                        .where(BasicDataItemTable.BASIC_DATA_ITEM.TYPE_NUMBER.eq(typeNumber)));
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

        // 缓存 key 依赖方法内部计算逻辑，使用编程方式清除
        cache.remove(CACHE_PREFIX + typeNumber);

        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "基础数据ID不能为空");
        }
        BasicDataEntity entity = mapper.selectOneById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "基础数据不存在");
        }
        mapper.deleteById(id);
    }
}
