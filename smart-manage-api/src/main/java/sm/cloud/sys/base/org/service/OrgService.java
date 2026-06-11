package sm.cloud.sys.base.org.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.base.org.domain.entity.OrgEntity;
import sm.cloud.sys.base.org.domain.form.OrgListForm;
import sm.cloud.sys.base.org.domain.vo.OrgListVO;
import sm.cloud.sys.base.org.mapper.OrgMapper;
import sm.system.response.PageResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrgService {
	private final OrgMapper mapper;

	public PageResult<OrgListVO> listPage(OrgListForm form) {
		LambdaQueryWrapper<OrgEntity> qw = new LambdaQueryWrapper<OrgEntity>()
				.orderByAsc(OrgEntity::getSort)
				.orderByAsc(OrgEntity::getId);
		Page<OrgEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<OrgEntity> result = mapper.selectPage(page, qw);
		List<OrgListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), vos);
	}

	private OrgListVO toListVo(OrgEntity e) {
		OrgListVO vo = new OrgListVO();
		vo.setId(e.getId());
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		vo.setParentId(e.getParentId());
		vo.setSort(e.getSort());
		return vo;
	}
}
