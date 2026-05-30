package sm.system.response;

import lombok.Getter;

/**
 * @author Chekfu
 */
@Getter
public enum ResultEnum {
	SUCCESS(200, ""),
	BAD_REQUEST(400, "错误的请求"),
	UNAUTHORIZED(401, "未登录"),
	PERMISSION_ERROR(402, "没有权限"),
	FORBIDDEN(403, "请求过于频繁，请稍后再试"),
	NOT_FOUND(404, "资源不存在"),
	PARAM_ERROR(405, "参数异常"),
	BILL_STATUS_ERROR(406, "单据状态不允许当前操作"),
	SERVER_ERROR(500, "系统异常，请稍候再试"),
	SQL_ERROR(501, "SQL异常，请联系管理员处理"),
	CONFIG_ERROR(502, "系统配置异常，请联系管理员处理"),
	CAPTCHA_ERROR(600, "验证码错误"),
	CAPTCHA_EXPIRE(601, "验证码已过期"),
	;

	/**
	 * 错误码
	 */
	private final int code;
	/**
	 * 错误消息
	 */
	private final String msg;

	ResultEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
