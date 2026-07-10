package sm.domain.sys.base.cloud.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.cloud.model.entity.CloudEntity;
import sm.domain.sys.base.cloud.model.form.CloudListForm;
import sm.domain.sys.base.cloud.model.form.CloudSelectForm;
import sm.domain.sys.base.cloud.model.form.CloudSaveForm;
import sm.domain.sys.base.cloud.model.vo.CloudCreateNewDataVO;
import sm.domain.sys.base.cloud.model.vo.CloudDetailVO;
import sm.domain.sys.base.cloud.model.vo.CloudListVO;
import sm.domain.sys.base.cloud.model.vo.CloudSelectVO;
import sm.domain.sys.base.cloud.mapper.CloudMapper;
import sm.system.exception.BizException;
import sm.system.aop.log.BizLog;
import sm.system.response.PageData;
import sm.system.response.ResultEnum;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudService {
	private final CloudMapper mapper;
	private final CloudTxService txService;

	public PageData<CloudListVO> listPage(CloudListForm form) {
		LambdaQueryWrapper<CloudEntity> qw = new LambdaQueryWrapper<CloudEntity>();
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(condition -> condition.like(CloudEntity::getName, kw).or().like(CloudEntity::getNumber, kw));
		}
		if (form.getEnableFlag() != null) {
			qw.eq(CloudEntity::getEnableFlag, form.getEnableFlag());
		}
		qw.orderByAsc(CloudEntity::getSeq).orderByAsc(CloudEntity::getId);
		Page<CloudEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<CloudEntity> result = mapper.selectPage(page, qw);
		List<CloudListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
		return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), vos);
	}

	public PageData<CloudSelectVO> select(CloudSelectForm form) {
		LambdaQueryWrapper<CloudEntity> qw = new LambdaQueryWrapper<CloudEntity>();
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(condition -> condition.like(CloudEntity::getName, kw).or().like(CloudEntity::getNumber, kw));
		}
		if (form.getEnableFlag() != null) {
			qw.eq(CloudEntity::getEnableFlag, form.getEnableFlag());
		}
		qw.orderByAsc(CloudEntity::getSeq).orderByAsc(CloudEntity::getId);
		Page<CloudEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<CloudEntity> result = mapper.selectPage(page, qw);
		List<CloudSelectVO> vos = result.getRecords().stream().map(this::toSelectVo).collect(Collectors.toList());
		return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), vos);
	}

	private CloudListVO toListVo(CloudEntity e) {
		CloudListVO vo = new CloudListVO();
		vo.setId(e.getId());
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		vo.setSeq(e.getSeq());
		vo.setEnableFlag(e.getEnableFlag());
		vo.setCreateTime(e.getCreateTime());
		vo.setUpdateTime(e.getUpdateTime());
		return vo;
	}

	private CloudSelectVO toSelectVo(CloudEntity e) {
		CloudSelectVO vo = new CloudSelectVO();
		vo.setId(e.getId());
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		vo.setEnableFlag(e.getEnableFlag());
		return vo;
	}

	public CloudEntity getById(Long id) {
		return mapper.selectById(id);
	}

	public CloudDetailVO getDetail(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "云ID不能为空");
		}
		CloudEntity entity = mapper.selectById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "云不存在");
		}
		return toDetailVo(entity);
	}

	private CloudDetailVO toDetailVo(CloudEntity e) {
		CloudDetailVO vo = new CloudDetailVO();
		vo.setId(String.valueOf(e.getId()));
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		vo.setSeq(e.getSeq());
		vo.setEnableFlag(e.getEnableFlag());
		vo.setCreateTime(e.getCreateTime());
		vo.setUpdateTime(e.getUpdateTime());
		vo.setCreateUser(e.getCreateUser());
		vo.setUpdateUser(e.getUpdateUser());
		return vo;
	}

	public CloudCreateNewDataVO createNewData() {
		CloudCreateNewDataVO vo = new CloudCreateNewDataVO();
		vo.setSeq(99);
		vo.setEnableFlag(true);
		return vo;
	}

	@BizLog("保存云")
	public Long save(CloudSaveForm form) {
		return txService.save(form);
	}

	@BizLog("删除云")
	public void deleteById(Long id) {
		txService.deleteById(id);
	}
}
