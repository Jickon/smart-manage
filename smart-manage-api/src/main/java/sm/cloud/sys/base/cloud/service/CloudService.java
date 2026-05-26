package sm.cloud.sys.base.cloud.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.cloud.domain.entity.CloudEntity;
import sm.cloud.sys.base.cloud.domain.entity.table.CloudTable;
import sm.cloud.sys.base.cloud.domain.form.CloudListForm;
import sm.cloud.sys.base.cloud.domain.form.CloudSelectForm;
import sm.cloud.sys.base.cloud.domain.form.CloudSaveForm;
import sm.cloud.sys.base.cloud.domain.vo.CloudCreateNewDataVO;
import sm.cloud.sys.base.cloud.domain.vo.CloudDetailVO;
import sm.cloud.sys.base.cloud.domain.vo.CloudListVO;
import sm.cloud.sys.base.cloud.domain.vo.CloudSelectVO;
import sm.cloud.sys.base.cloud.mapper.CloudMapper;
import sm.system.response.PageResult;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudService {
	private final CloudMapper mapper;

	public PageResult<CloudListVO> listPage(CloudListForm form) {
		QueryWrapper qw = QueryWrapper.create().from(CloudTable.CLOUD);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(CloudTable.CLOUD.NAME.like(kw).or(CloudTable.CLOUD.NUMBER.like(kw)));
		}
		qw.orderBy(CloudTable.CLOUD.SEQ, true).orderBy(CloudTable.CLOUD.ID, true);
		Page<CloudEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<CloudEntity> result = mapper.paginate(page, qw);
		List<CloudListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
	}

	public PageResult<CloudSelectVO> select(CloudSelectForm form) {
		QueryWrapper qw = QueryWrapper.create().from(CloudTable.CLOUD);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(CloudTable.CLOUD.NAME.like(kw).or(CloudTable.CLOUD.NUMBER.like(kw)));
		}
		if (form.getEnableFlag() != null) {
			qw.and(CloudTable.CLOUD.ENABLE_FLAG.eq(form.getEnableFlag()));
		}
		qw.orderBy(CloudTable.CLOUD.SEQ, true).orderBy(CloudTable.CLOUD.ID, true);
		Page<CloudEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<CloudEntity> result = mapper.paginate(page, qw);
		List<CloudSelectVO> vos = result.getRecords().stream().map(this::toSelectVo).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
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
		return mapper.selectOneById(id);
	}

	public CloudDetailVO getDetail(Long id) {
		CloudEntity entity = mapper.selectOneById(id);
		return entity == null ? null : toDetailVo(entity);
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

	@Transactional(rollbackFor = Exception.class)
	public Long save(CloudSaveForm form) {
		CloudEntity e;
		if (form.getId() != null) {
			e = mapper.selectOneById(form.getId());
			if (e == null) {
				return null;
			}
		} else {
			e = new CloudEntity();
		}
		e.setName(form.getName());
		e.setNumber(form.getNumber());
		e.setSeq(form.getSeq() != null ? form.getSeq() : 99);
		e.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
		if (form.getId() == null) {
			mapper.insert(e);
		} else {
			mapper.update(e);
		}
		return e.getId();
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long id) {
		mapper.deleteById(id);
	}
}

