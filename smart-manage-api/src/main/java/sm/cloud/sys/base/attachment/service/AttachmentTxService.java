package sm.cloud.sys.base.attachment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sm.cloud.sys.base.attachment.domain.entity.AttachmentEntity;
import sm.cloud.sys.base.attachment.domain.entity.BizAttachmentEntity;
import sm.cloud.sys.base.attachment.domain.form.AttachmentPromoteForm;
import sm.cloud.sys.base.attachment.domain.vo.AttachmentVO;
import sm.cloud.sys.base.attachment.mapper.AttachmentMapper;
import sm.cloud.sys.base.attachment.mapper.BizAttachmentMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;
import sm.system.storage.FileStorageService;
import sm.system.storage.FileStorageServiceFactory;
import sm.system.storage.FileStoreResult;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * 附件事务服务 —— 所有写操作在类级别事务中执行
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AttachmentTxService {
    private final AttachmentMapper mapper;
    private final BizAttachmentMapper bizMapper;
    private final FileStorageServiceFactory storageFactory;

    /** 上传附件：传 bizType 时存入临时目录（需 promote），否则直接存 sys 系统目录 */
    public AttachmentVO upload(MultipartFile file, String bizType) throws IOException {
        FileStorageService storage = storageFactory.getService();
        boolean isTemp = bizType != null && !bizType.isBlank();
        FileStoreResult result = isTemp ? storage.storeTemp(file) : storage.store("sys", file);
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        AttachmentEntity entity = new AttachmentEntity();
        entity.setOriginalName(originalName);
        entity.setStoredName(result.getStoredName());
        entity.setStoredPath(result.getStoredPath());
        entity.setFileSize(result.getFileSize());
        entity.setMimeType(file.getContentType());
        entity.setFileExt(ext);
        entity.setStorageType(storage.getType());
        entity.setIsTemp(isTemp);
        mapper.insert(entity);
        if (isTemp) {
            BizAttachmentEntity biz = new BizAttachmentEntity();
            biz.setBizType(bizType);
            biz.setBizId(null);
            biz.setAttachmentId(entity.getId());
            biz.setSort(0);
            bizMapper.insert(biz);
        }
        log.info("附件上传: id={}, name={}, temp={}", entity.getId(), originalName, isTemp);
        return toVo(entity);
    }

    /** 提升附件：关联业务单据 + 移出临时目录 */
    public void promote(AttachmentPromoteForm form) throws IOException {
        FileStorageService storage = storageFactory.getService();
        for (Long attachmentId : form.getAttachmentIds()) {
            AttachmentEntity entity = mapper.selectById(attachmentId);
            if (entity == null) {
                throw new BizException("附件不存在: " + attachmentId);
            }
            if (entity.getIsTemp() != null && entity.getIsTemp()) {
                // 移动文件：temp → biz/{bizType}
                String newPath = storage.promote(entity.getStoredPath(), "biz/" + form.getBizType());
                entity.setStoredPath(newPath);
                entity.setIsTemp(false);
                mapper.updateById(entity);
            }
            // 更新业务映射
            BizAttachmentEntity biz = selectBizByAttachmentId(attachmentId);
            if (biz != null) {
                biz.setBizId(form.getBizId());
                bizMapper.updateById(biz);
            } else {
                BizAttachmentEntity newBiz = new BizAttachmentEntity();
                newBiz.setBizType(form.getBizType());
                newBiz.setBizId(form.getBizId());
                newBiz.setAttachmentId(attachmentId);
                newBiz.setSort(0);
                bizMapper.insert(newBiz);
            }
        }
        log.info("附件提升: ids={}, bizType={}, bizId={}", form.getAttachmentIds(), form.getBizType(), form.getBizId());
    }

    /** 删除附件（物理文件 + 映射 + 元数据） */
    public void delete(Long id) throws IOException {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "附件 id 不能为空");
        }
        AttachmentEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "附件不存在：" + id);
        }
        FileStorageService storage = storageFactory.getService();
        storage.delete(entity.getStoredPath());
        // 删除业务映射
        bizMapper.delete(new LambdaQueryWrapper<BizAttachmentEntity>()
                .eq(BizAttachmentEntity::getAttachmentId, id));
        mapper.deleteById(id);
        log.info("附件删除: id={}, path={}", id, entity.getStoredPath());
    }

    /** 按附件 ID 查询业务映射 */
    private BizAttachmentEntity selectBizByAttachmentId(Long attachmentId) {
        return bizMapper.selectOne(new LambdaQueryWrapper<BizAttachmentEntity>()
                .eq(BizAttachmentEntity::getAttachmentId, attachmentId));
    }

    private AttachmentVO toVo(AttachmentEntity entity) {
        AttachmentVO vo = new AttachmentVO();
        vo.setId(entity.getId());
        vo.setOriginalName(entity.getOriginalName());
        vo.setFileSize(entity.getFileSize());
        vo.setMimeType(entity.getMimeType());
        vo.setFileExt(entity.getFileExt());
        vo.setIsTemp(entity.getIsTemp());
        vo.setUrl(storageFactory.getService().getAccessUrl(entity.getStoredPath()));
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return vo;
    }
}
