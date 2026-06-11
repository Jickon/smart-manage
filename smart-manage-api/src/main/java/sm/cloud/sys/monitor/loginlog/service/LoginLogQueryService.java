package sm.cloud.sys.monitor.loginlog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sm.cloud.sys.monitor.loginlog.domain.entity.LoginLogEntity;
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
		LambdaQueryWrapper<LoginLogEntity> qw = new LambdaQueryWrapper<LoginLogEntity>();
		if (StringUtils.hasText(form.getKeyword())) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(condition -> condition.like(LoginLogEntity::getUsername, kw).or().like(LoginLogEntity::getNickname, kw));
		}
		if (form.getSuccess() != null) {
			qw.eq(LoginLogEntity::getSuccess, form.getSuccess());
		}
		if (form.getBeginTime() != null) {
			qw.ge(LoginLogEntity::getCreateTime, form.getBeginTime());
		}
		if (form.getEndTime() != null) {
			qw.le(LoginLogEntity::getCreateTime, form.getEndTime());
		}
		qw.orderByDesc(LoginLogEntity::getCreateTime);
		Page<LoginLogEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<LoginLogEntity> result = loginLogMapper.selectPage(page, qw);
		var records = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), records);
	}

	public LoginLogListVO getById(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "登录日志ID不能为空");
		}
		LoginLogEntity entity = loginLogMapper.selectById(id);
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
