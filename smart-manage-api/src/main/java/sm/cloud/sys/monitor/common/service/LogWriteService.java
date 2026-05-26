package sm.cloud.sys.monitor.common.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sm.cloud.sys.monitor.common.util.LogPayloadUtil;
import sm.cloud.sys.monitor.loginlog.domain.entity.LoginLogEntity;
import sm.cloud.sys.monitor.loginlog.mapper.LoginLogMapper;
import sm.cloud.sys.monitor.operatelog.domain.entity.OperateLogEntity;
import sm.cloud.sys.monitor.operatelog.mapper.OperateLogMapper;

import java.time.LocalDateTime;

/**
 * 异步写入日志（公共能力）
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LogWriteService {
	private final LoginLogMapper loginLogMapper;
	private final OperateLogMapper operateLogMapper;
	@Resource
	@Qualifier("logTaskExecutor")
	private ThreadPoolTaskExecutor logTaskExecutor;

	public void writeLogin(LoginLogEntity e) {
		if (e.getCreateTime() == null) {
			e.setCreateTime(LocalDateTime.now());
		}
		runAsync(() -> loginLogMapper.insert(e));
	}

	public void writeOper(OperateLogEntity entity) {
		if (entity.getCreateTime() == null) {
			entity.setCreateTime(LocalDateTime.now());
		}
		runAsync(() -> operateLogMapper.insert(entity));
	}

	/**
	 * 登录失败（在 Web 请求线程中采集上下文后入队）
	 */
	public void writeLoginFailed(String username, String failReason, String ip, String userAgent) {
		LoginLogEntity entity = new LoginLogEntity();
		entity.setUsername(username);
		entity.setEventType("LOGIN");
		entity.setSuccess(false);
		entity.setFailReason(truncateMsg(failReason));
		entity.setIp(ip);
		entity.setUserAgent(StringUtils.hasText(userAgent) ? userAgent : null);
		entity.setCreateTime(LocalDateTime.now());
		writeLogin(entity);
	}

	private void runAsync(Runnable r) {
		if (logTaskExecutor == null) {
			r.run();
			return;
		}
		logTaskExecutor.execute(() -> {
			try {
				r.run();
			} catch (Exception e) {
				log.warn("异步日志写入失败", e);
			}
		});
	}

	private String truncateMsg(String s) {
		return s == null ? null : LogPayloadUtil.truncate(s, 500);
	}
}

