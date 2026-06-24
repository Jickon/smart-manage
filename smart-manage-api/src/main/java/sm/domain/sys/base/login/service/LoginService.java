package sm.domain.sys.base.login.service;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sm.domain.sys.base.common.config.CaptchaConfig;
import sm.domain.sys.base.common.constant.RedisKeyConstant;
import sm.domain.sys.base.common.util.CaptchaUtil;
import sm.domain.sys.base.login.model.form.LoginForm;
import sm.domain.sys.base.login.model.vo.CaptchaVO;
import sm.domain.sys.base.login.model.vo.LoginVO;
import sm.domain.sys.base.menu.service.MenuService;
import sm.domain.sys.base.user.model.entity.UserEntity;
import sm.domain.sys.base.user.service.UserService;
import sm.domain.sys.monitor.common.service.LogWriteService;
import sm.system.exception.BizException;
import sm.system.helper.SM2Helper;
import sm.system.response.ResultEnum;
import sm.system.util.ServletUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Chekfu
 */
@Service
@Slf4j
public class LoginService {
	private final CaptchaConfig captchaConfig;
	private final UserService userService;
	private final MenuService menuService;
	private final LogWriteService logWriteService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public LoginService(CaptchaConfig captchaConfig, UserService userService, MenuService menuService, LogWriteService logWriteService) {
		this.captchaConfig = captchaConfig;
		this.userService = userService;
		this.menuService = menuService;
		this.logWriteService = logWriteService;
	}

	public LoginVO login(LoginForm form) {
		// 验证码校验
		String decryptedCaptcha = form.getCaptcha() != null ? SM2Helper.decrypt(form.getCaptcha()) : null;
		String captchaKey = RedisKeyConstant.CAPTCHA + form.getCaptchaId();
		String captcha = (String) redisTemplate.opsForValue().get(captchaKey);
		if (captcha == null) {
			throw new BizException(ResultEnum.CAPTCHA_EXPIRE);
		}
		if (!captcha.equalsIgnoreCase(decryptedCaptcha)) {
			throw new BizException(ResultEnum.CAPTCHA_ERROR);
		}
		redisTemplate.delete(captchaKey);

		// SM2 解密前端密码
		String decryptedPassword = SM2Helper.decrypt(form.getPassword());
		LoginVO vo = userService.login(form.getUsername(), decryptedPassword);
		if (vo.getToken() == null && StringUtils.hasText(form.getUsername())) {
			String ip = null;
			String ua = null;
			try {
				ip = ServletUtil.getClientIp();
				ua = ServletUtil.getRequest().getHeader("User-Agent");
			} catch (Exception e) {
				log.warn("获取客户端IP/UA失败", e);
			}
			logWriteService.writeLoginFailed(form.getUsername(), vo.getMsg(), ip, ua);
		}
		return vo;
	}

	public CaptchaVO captcha() throws IOException {
		// 生成验证码ID
		String captchaId = UUID.randomUUID().toString();
		// 生成验证码
		String captcha = CaptchaUtil.generateCharCaptcha(captchaConfig.getLength());
		// 生成验证码图片
		BufferedImage image = CaptchaUtil.generateCaptchaImage(captcha, captchaConfig.getWidth(), captchaConfig.getHeight());

		// 将验证码存入Redis
		redisTemplate.opsForValue().set(RedisKeyConstant.CAPTCHA + captchaId, captcha, captchaConfig.getExpire(), TimeUnit.SECONDS);

		// 将图片转换为Base64
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpeg", outputStream);
		String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
		String imageData = "data:image/jpeg;base64," + base64Image;

		// 返回VO对象
		CaptchaVO vo = new CaptchaVO();
		vo.setCaptchaId(captchaId);
		vo.setImageData(imageData);
		return vo;
	}

	/**
	 * 用户注册暂未开放，后续实现。
	 */
	public void register(UserEntity user) {
		throw new UnsupportedOperationException("注册功能暂未开放");
	}

	public void logout() {
		StpUtil.logout();
	}
}
