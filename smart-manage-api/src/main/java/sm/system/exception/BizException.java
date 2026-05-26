package sm.system.exception;


import lombok.Getter;
import sm.system.response.ResultEnum;

/**
 * @author Chekfu
 */
@Getter
public class BizException extends RuntimeException {
	/**
	 * 异常编号
	 */
	private final int code;
	/**
	 * 异常信息
	 */
	private final String msg;


	public BizException(String msg) {
		super(msg);
		this.code = ResultEnum.SERVER_ERROR.getCode();
		this.msg = msg;
	}

	public BizException(int code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}

	public BizException(ResultEnum resultEnum) {
		this(resultEnum.getCode(), resultEnum.getMsg());
	}

	/**
	 * 构造函数
	 *
	 * @param resultEnum 异常码
	 */
	public BizException(ResultEnum resultEnum, String msg) {
		this(resultEnum.getCode(), resultEnum.getMsg() + "：" + msg);
	}

}
