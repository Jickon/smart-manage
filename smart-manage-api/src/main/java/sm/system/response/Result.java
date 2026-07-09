package sm.system.response;

import lombok.Getter;
import sm.system.util.TraceIdUtil;

/**
 * 统一接口响应体。
 *
 * @author Chekfu
 */
@Getter
public class Result<T> {
    private final Integer code;
    private final String msg;
    private final T data;
    private final String traceId;

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.traceId = TraceIdUtil.getTraceId();
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), "", data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return error(ResultEnum.SERVER_ERROR.getCode(), message);
    }

    public static <T> Result<T> error(ResultEnum resultEnum) {
        return error(resultEnum.getCode(), resultEnum.getMsg());
    }

    public static <T> Result<T> error(ResultEnum resultEnum, String errorMessage) {
        return error(resultEnum.getCode(), resultEnum.getMsg() + "：" + errorMessage);
    }
}
