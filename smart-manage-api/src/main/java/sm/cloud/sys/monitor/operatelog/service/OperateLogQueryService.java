package sm.cloud.sys.monitor.operatelog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sm.cloud.sys.monitor.operatelog.domain.entity.OperateLogEntity;
import sm.cloud.sys.monitor.operatelog.domain.form.OperateLogListForm;
import sm.cloud.sys.monitor.operatelog.domain.vo.OperateLogDetailVO;
import sm.cloud.sys.monitor.operatelog.domain.vo.OperateLogListVO;
import sm.cloud.sys.monitor.operatelog.mapper.OperateLogMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperateLogQueryService {
	private final OperateLogMapper mapper;

	public PageResult<OperateLogListVO> listPage(OperateLogListForm form) {
		LambdaQueryWrapper<OperateLogEntity> qw = new LambdaQueryWrapper<OperateLogEntity>();
		if (StringUtils.hasText(form.getKeyword())) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(condition -> condition.like(OperateLogEntity::getRequestUri, kw).or().like(OperateLogEntity::getMethodName, kw).or().like(OperateLogEntity::getBizName, kw));
		}
		if (form.getSuccess() != null) {
			qw.eq(OperateLogEntity::getSuccess, form.getSuccess());
		}
		if (form.getBeginTime() != null) {
			qw.ge(OperateLogEntity::getCreateTime, form.getBeginTime());
		}
		if (form.getEndTime() != null) {
			qw.le(OperateLogEntity::getCreateTime, form.getEndTime());
		}
		qw.orderByDesc(OperateLogEntity::getCreateTime);
		Page<OperateLogEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<OperateLogEntity> result = mapper.selectPage(page, qw);
		var records = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), records);
	}

	public OperateLogDetailVO getById(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "操作日志ID不能为空");
		}
		OperateLogEntity entity = mapper.selectById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "操作日志不存在");
		}
		return toDetail(entity);
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
