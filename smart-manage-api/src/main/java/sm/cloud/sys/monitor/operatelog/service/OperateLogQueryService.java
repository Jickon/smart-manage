package sm.cloud.sys.monitor.operatelog.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sm.cloud.sys.monitor.operatelog.domain.entity.OperateLogEntity;
import sm.cloud.sys.monitor.operatelog.domain.entity.table.OperateLogTable;
import sm.cloud.sys.monitor.operatelog.domain.form.OperateLogListForm;
import sm.cloud.sys.monitor.operatelog.domain.vo.OperateLogDetailVO;
import sm.cloud.sys.monitor.operatelog.domain.vo.OperateLogListVO;
import sm.cloud.sys.monitor.operatelog.mapper.OperateLogMapper;
import sm.system.response.PageResult;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperateLogQueryService {
	private final OperateLogMapper mapper;

	public PageResult<OperateLogListVO> listPage(OperateLogListForm form) {
		QueryWrapper qw = QueryWrapper.create().from(OperateLogTable.OPERATE_LOG);
		if (StringUtils.hasText(form.getKeyword())) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(OperateLogTable.OPERATE_LOG.REQUEST_URI.like(kw)
					.or(OperateLogTable.OPERATE_LOG.METHOD_NAME.like(kw))
					.or(OperateLogTable.OPERATE_LOG.BIZ_NAME.like(kw)));
		}
		if (form.getSuccess() != null) {
			qw.and(OperateLogTable.OPERATE_LOG.SUCCESS.eq(form.getSuccess()));
		}
		if (form.getBeginTime() != null) {
			qw.and(OperateLogTable.OPERATE_LOG.CREATE_TIME.ge(form.getBeginTime()));
		}
		if (form.getEndTime() != null) {
			qw.and(OperateLogTable.OPERATE_LOG.CREATE_TIME.le(form.getEndTime()));
		}
		qw.orderBy(OperateLogTable.OPERATE_LOG.CREATE_TIME, false);
		Page<OperateLogEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<OperateLogEntity> result = mapper.paginate(page, qw);
		var records = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), records);
	}

	public OperateLogDetailVO getById(Long id) {
		OperateLogEntity e = mapper.selectOneById(id);
		if (e == null) {
			return null;
		}
		return toDetail(e);
	}

	private OperateLogListVO toListVo(OperateLogEntity e) {
		OperateLogListVO vo = new OperateLogListVO();
		vo.setId(e.getId());
		vo.setBizName(e.getBizName());
		vo.setSuccess(e.getSuccess());
		vo.setErrorMsg(e.getErrorMsg());
		vo.setRequestMethod(e.getRequestMethod());
		vo.setRequestUri(e.getRequestUri());
		vo.setIp(e.getIp());
		vo.setClassName(e.getClassName());
		vo.setMethodName(e.getMethodName());
		vo.setDurationMs(e.getDurationMs());
		vo.setUsername(e.getUsername());
		vo.setCreateTime(e.getCreateTime());
		return vo;
	}

	private OperateLogDetailVO toDetail(OperateLogEntity e) {
		OperateLogDetailVO vo = new OperateLogDetailVO();
		vo.setId(e.getId());
		vo.setBizName(e.getBizName());
		vo.setSuccess(e.getSuccess());
		vo.setErrorMsg(e.getErrorMsg());
		vo.setRequestMethod(e.getRequestMethod());
		vo.setRequestUri(e.getRequestUri());
		vo.setIp(e.getIp());
		vo.setUserAgent(e.getUserAgent());
		vo.setClassName(e.getClassName());
		vo.setMethodName(e.getMethodName());
		vo.setDurationMs(e.getDurationMs());
		vo.setRequestParams(e.getRequestParams());
		vo.setResponseBody(e.getResponseBody());
		vo.setUserId(e.getUserId());
		vo.setUsername(e.getUsername());
		vo.setCreateTime(e.getCreateTime());
		return vo;
	}
}
