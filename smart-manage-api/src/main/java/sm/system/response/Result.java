package sm.system.response;

import lombok.Data;
import sm.system.util.TraceIdUtil;

/**
 * @author Chekfu
 */
@Data
public class Result<T> {
	private Integer code;
	private String msg;
	private T data;
	private String traceId;

	private Result() {
	}

	public static <T> Result<T> success(T data) {
		Result<T> result = new Result<>();
		result.setCode(ResultEnum.SUCCESS.getCode());
		result.setMsg("");
		result.setData(data);
		result.setTraceId(TraceIdUtil.getTraceId());
		return result;
	}

	public static <T> Result<T> success() {
		return success(null);
	}

	public static <T> Result<T> error(Integer code, String message) {
		Result<T> result = new Result<>();
		result.setCode(code);
		result.setMsg(message);
		result.setTraceId(TraceIdUtil.getTraceId());
		return result;
	}

	public static <T> Result<T> error(String message) {
		return error(ResultEnum.SERVER_ERROR.getCode(), message);
	}

	public static <T> Result<T> error(Throwable e) {
		return error(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
	}

	public static <T> Result<T> error(ResultEnum resultEnum) {
		return error(resultEnum.getCode(), resultEnum.getMsg());
	}

	public static <T> Result<T> error(ResultEnum resultEnum, String errorMessage) {
		return error(resultEnum.getCode(), resultEnum.getMsg() + "：" + errorMessage);
	}
} 