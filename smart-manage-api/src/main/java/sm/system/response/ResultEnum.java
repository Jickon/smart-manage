package sm.system.response;

import lombok.Getter;

/**
 * 系统统一业务响应码。
 *
 * @author Chekfu
 */
@Getter
public enum ResultEnum {
    SUCCESS(0, ""),
    BAD_REQUEST(100400, "错误的请求"),
    UNAUTHORIZED(100401, "未登录"),
    PERMISSION_ERROR(100403, "没有权限"),
    REQUEST_LIMIT(100429, "请求过于频繁，请稍后再试"),
    NOT_FOUND(100404, "资源不存在"),
    PARAM_ERROR(100422, "参数异常"),
    SERVER_ERROR(100500, "系统异常，请稍候再试"),
    SQL_ERROR(100501, "SQL异常，请联系管理员处理"),
    CONFIG_ERROR(100502, "系统配置异常，请联系管理员处理"),
    CAPTCHA_ERROR(101600, "验证码错误"),
    CAPTCHA_EXPIRE(101601, "验证码已过期"),
    BILL_STATUS_ERROR(200001, "单据状态不允许当前操作"),
    ;

    /**
     * 业务响应码。成功固定为 0，错误码不复用 HTTP 状态码。
     */
    private final int code;

    /**
     * 响应消息。
     */
    private final String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
