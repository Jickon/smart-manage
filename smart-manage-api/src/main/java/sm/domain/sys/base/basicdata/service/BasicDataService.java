package sm.domain.sys.base.basicdata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.basicdataitem.model.entity.BasicDataItemEntity;
import sm.domain.sys.base.basicdataitem.model.vo.BasicDataItemListVO;
import sm.domain.sys.base.basicdataitem.mapper.BasicDataItemMapper;
import sm.domain.sys.base.basicdata.model.entity.BasicDataEntity;
import sm.domain.sys.base.basicdata.model.form.BasicDataListForm;
import sm.domain.sys.base.basicdata.model.form.BasicDataSaveForm;
import sm.domain.sys.base.basicdata.model.vo.BasicDataCreateNewDataVO;
import sm.domain.sys.base.basicdata.model.vo.BasicDataDetailVO;
import sm.domain.sys.base.basicdata.model.vo.BasicDataListVO;
import sm.domain.sys.base.basicdata.mapper.BasicDataMapper;
import sm.system.exception.BizException;
import sm.system.response.PageData;
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
    private final BasicDataTxService txService;

    public PageData<BasicDataListVO> listPage(BasicDataListForm form) {
        LambdaQueryWrapper<BasicDataEntity> qw = new LambdaQueryWrapper<BasicDataEntity>();
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.and(condition -> condition.like(BasicDataEntity::getName, kw).or().like(BasicDataEntity::getNumber, kw));
        }
        qw.orderByAsc(BasicDataEntity::getNumber);
        Page<BasicDataEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
        Page<BasicDataEntity> result = mapper.selectPage(page, qw);
        List<BasicDataListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
        return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), vos);
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
        BasicDataEntity entity = mapper.selectById(id);
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
        List<BasicDataItemEntity> items = itemMapper.selectList(
                new LambdaQueryWrapper<BasicDataItemEntity>()
                        .eq(BasicDataItemEntity::getTypeNumber, entity.getNumber())
                        .orderByAsc(BasicDataItemEntity::getSort));
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

    public Long save(BasicDataSaveForm form) {
        return txService.save(form);
    }

    public void deleteById(Long id) {
        txService.deleteById(id);
    }
}
