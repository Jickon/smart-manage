package sm.cloud.sys.monitor.loginlog.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sm.cloud.sys.monitor.loginlog.domain.entity.LoginLogEntity;
import sm.cloud.sys.monitor.loginlog.domain.entity.table.LoginLogTable;
import sm.cloud.sys.monitor.loginlog.domain.form.LoginLogListForm;
import sm.cloud.sys.monitor.loginlog.domain.vo.LoginLogListVO;
import sm.cloud.sys.monitor.loginlog.mapper.LoginLogMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginLogQueryService {
	private final LoginLogMapper loginLogMapper;

	public PageResult<LoginLogListVO> listPage(LoginLogListForm form) {
		QueryWrapper qw = QueryWrapper.create().from(LoginLogTable.LOGIN_LOG);
		if (StringUtils.hasText(form.getKeyword())) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(LoginLogTable.LOGIN_LOG.USERNAME.like(kw).or(LoginLogTable.LOGIN_LOG.NICKNAME.like(kw)));
		}
		if (form.getSuccess() != null) {
			qw.and(LoginLogTable.LOGIN_LOG.SUCCESS.eq(form.getSuccess()));
		}
		if (form.getBeginTime() != null) {
			qw.and(LoginLogTable.LOGIN_LOG.CREATE_TIME.ge(form.getBeginTime()));
		}
		if (form.getEndTime() != null) {
			qw.and(LoginLogTable.LOGIN_LOG.CREATE_TIME.le(form.getEndTime()));
		}
		qw.orderBy(LoginLogTable.LOGIN_LOG.CREATE_TIME, false);
		Page<LoginLogEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<LoginLogEntity> result = loginLogMapper.paginate(page, qw);
		var records = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), records);
	}

	public LoginLogListVO getById(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "登录日志ID不能为空");
		}
		LoginLogEntity entity = loginLogMapper.selectOneById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "登录日志不存在");
		}
		return toVo(entity);
	}

	private LoginLogListVO toVo(LoginLogEntity e) {
		LoginLogListVO vo = new LoginLogListVO();
		vo.setId(e.getId());
		vo.setUserId(e.getUserId());
		vo.setUsername(e.getUsername());
		vo.setNickname(e.getNickname());
		vo.setEventType(e.getEventType());
		vo.setSuccess(e.getSuccess());
		vo.setFailReason(e.getFailReason());
		vo.setIp(e.getIp());
		vo.setUserAgent(e.getUserAgent());
		vo.setTokenHint(e.getTokenHint());
		vo.setCreateTime(e.getCreateTime());
		return vo;
	}
}
