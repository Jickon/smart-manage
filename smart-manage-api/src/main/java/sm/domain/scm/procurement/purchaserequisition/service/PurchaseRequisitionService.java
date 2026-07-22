package sm.domain.scm.procurement.purchaserequisition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sm.domain.scm.procurement.purchaserequisition.mapper.PurchaseRequisitionEntryMapper;
import sm.domain.scm.procurement.purchaserequisition.mapper.PurchaseRequisitionMapper;
import sm.domain.scm.procurement.purchaserequisition.model.entity.PurchaseRequisitionEntity;
import sm.domain.scm.procurement.purchaserequisition.model.entity.PurchaseRequisitionEntryEntity;
import sm.domain.scm.procurement.purchaserequisition.model.form.PurchaseRequisitionListForm;
import sm.domain.scm.procurement.purchaserequisition.model.form.PurchaseRequisitionSaveForm;
import sm.domain.scm.procurement.purchaserequisition.model.vo.PurchaseRequisitionCreateNewDataVO;
import sm.domain.scm.procurement.purchaserequisition.model.vo.PurchaseRequisitionDetailVO;
import sm.domain.scm.procurement.purchaserequisition.model.vo.PurchaseRequisitionEntryVO;
import sm.domain.scm.procurement.purchaserequisition.model.vo.PurchaseRequisitionListVO;
import sm.domain.sys.base.common.helper.UserHelper;
import sm.system.aop.log.BizLog;
import sm.system.enums.BillStatusEnum;
import sm.system.exception.BizException;
import sm.system.response.PageData;
import sm.system.response.ResultEnum;

import java.time.LocalDate;
import java.util.List;

/** 采购申请聚合的唯一公开服务。 */
@Service
@RequiredArgsConstructor
public class PurchaseRequisitionService {
    private final PurchaseRequisitionMapper mapper;
    private final PurchaseRequisitionEntryMapper entryMapper;
    private final PurchaseRequisitionTxService txService;

    public PageData<PurchaseRequisitionListVO> listPage(PurchaseRequisitionListForm form) {
        LambdaQueryWrapper<PurchaseRequisitionEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String keyword = form.getKeyword().trim();
            queryWrapper.and(condition -> condition.like(PurchaseRequisitionEntity::getNumber, keyword)
                    .or().like(PurchaseRequisitionEntity::getSubject, keyword));
        }
        queryWrapper.eq(form.getBillStatus() != null && !form.getBillStatus().isBlank(),
                PurchaseRequisitionEntity::getBillStatus, form.getBillStatus());
        queryWrapper.orderByDesc(PurchaseRequisitionEntity::getCreateTime);
        Page<PurchaseRequisitionEntity> page = mapper.selectPage(
                new Page<>(form.getPageNum(), form.getPageSize()), queryWrapper);
        List<PurchaseRequisitionListVO> records = page.getRecords().stream().map(this::toListVO).toList();
        return PageData.of(page.getTotal(), form.getPageNum(), form.getPageSize(), records);
    }

    public PurchaseRequisitionDetailVO detail(Long id) {
        PurchaseRequisitionEntity entity = requireEntity(id);
        PurchaseRequisitionDetailVO detailVO = new PurchaseRequisitionDetailVO();
        detailVO.setId(entity.getId());
        detailVO.setVersion(entity.getVersion());
        detailVO.setNumber(entity.getNumber());
        detailVO.setSubject(entity.getSubject());
        detailVO.setApplyOrgId(entity.getApplyOrgId());
        detailVO.setApplicantId(entity.getApplicantId());
        detailVO.setApplyDate(entity.getApplyDate());
        detailVO.setRequiredDate(entity.getRequiredDate());
        detailVO.setReason(entity.getReason());
        detailVO.setBillStatus(entity.getBillStatus());
        detailVO.setCreateTime(entity.getCreateTime());
        detailVO.setUpdateTime(entity.getUpdateTime());
        detailVO.setEntrys(entryMapper.selectList(new LambdaQueryWrapper<PurchaseRequisitionEntryEntity>()
                        .eq(PurchaseRequisitionEntryEntity::getParentId, id)
                        .orderByAsc(PurchaseRequisitionEntryEntity::getSort)
                        .orderByAsc(PurchaseRequisitionEntryEntity::getId))
                .stream().map(this::toEntryVO).toList());
        return detailVO;
    }

    public PurchaseRequisitionCreateNewDataVO createNewData() {
        PurchaseRequisitionCreateNewDataVO createNewDataVO = new PurchaseRequisitionCreateNewDataVO();
        createNewDataVO.setApplyOrgId(UserHelper.getCurrentOrgId());
        createNewDataVO.setApplicantId(UserHelper.getCurrentUserId());
        createNewDataVO.setApplyDate(LocalDate.now());
        createNewDataVO.setBillStatus(BillStatusEnum.SAVED.getValue());
        return createNewDataVO;
    }

    @BizLog("保存采购申请")
    public Long save(PurchaseRequisitionSaveForm form) {
        return txService.save(form);
    }

    @BizLog("提交采购申请")
    public void submit(Long id) {
        txService.submit(id);
    }

    @BizLog("删除采购申请")
    public void deleteById(Long id) {
        txService.deleteById(id);
    }

    private PurchaseRequisitionEntity requireEntity(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "采购申请ID不能为空");
        }
        PurchaseRequisitionEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "采购申请不存在");
        }
        return entity;
    }

    private PurchaseRequisitionListVO toListVO(PurchaseRequisitionEntity entity) {
        PurchaseRequisitionListVO listVO = new PurchaseRequisitionListVO();
        listVO.setId(entity.getId());
        listVO.setNumber(entity.getNumber());
        listVO.setSubject(entity.getSubject());
        listVO.setApplyDate(entity.getApplyDate());
        listVO.setRequiredDate(entity.getRequiredDate());
        listVO.setBillStatus(entity.getBillStatus());
        listVO.setCreateTime(entity.getCreateTime());
        return listVO;
    }

    private PurchaseRequisitionEntryVO toEntryVO(PurchaseRequisitionEntryEntity entity) {
        PurchaseRequisitionEntryVO entryVO = new PurchaseRequisitionEntryVO();
        entryVO.setId(entity.getId());
        entryVO.setMaterialName(entity.getMaterialName());
        entryVO.setSpecification(entity.getSpecification());
        entryVO.setUnit(entity.getUnit());
        entryVO.setQuantity(entity.getQuantity());
        entryVO.setRequiredDate(entity.getRequiredDate());
        entryVO.setRemark(entity.getRemark());
        entryVO.setSort(entity.getSort());
        return entryVO;
    }
}
