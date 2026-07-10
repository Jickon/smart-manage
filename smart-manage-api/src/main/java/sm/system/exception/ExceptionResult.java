package sm.system.exception;

import cn.dev33.satoken.exception.SaTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import sm.system.response.Result;
import sm.system.response.ResultEnum;
import sm.system.util.ServletUtil;

import java.util.stream.Collectors;

/**
 * 异常的返回结果
 *
 * @author Chekfu
 */
@Slf4j
public class ExceptionResult {

	public static Result<String> getExceptionResult(Throwable e) {
		Result<String> result;
		if (e instanceof BizException ex) {
			// 业务异常
			log.warn("业务异常: {}", ex.getMessage());
			result = Result.error(ex.getCode(), ex.getMessage());
		} else if (e instanceof DataIntegrityViolationException ex) {
			// 数据库唯一约束、外键约束等冲突统一转换为业务可识别的错误。
			log.warn("数据完整性冲突: {}", ex.getMostSpecificCause().getMessage());
			result = Result.error(ResultEnum.DATA_CONFLICT);
		} else if (e instanceof SerializationException ex) {
			// redis序列化异常
			log.error("redis序列化异常: {}", ex.getCause(), ex);
			result = Result.error(ResultEnum.SERVER_ERROR);
		} else if (e instanceof RedisConnectionFailureException ex) {
			// redis连接失败
			log.error("redis连接失败: {}", ex.getCause(), ex);
			result = Result.error(ResultEnum.CONFIG_ERROR, ex.getMessage());
		} else if (e instanceof NoResourceFoundException ex) {
			// 资源不存在
			log.error("资源不存在: {}", ex.getMessage(), ex);
			result = Result.error(ResultEnum.NOT_FOUND, ex.getMessage());
		} else if (e instanceof BadSqlGrammarException ex) {
			// SQL异常
			log.error("SQL异常: {}", ex.getMessage(), ex);
			result = Result.error(ResultEnum.SQL_ERROR);
		} else if (e instanceof MethodArgumentNotValidException ex) {
			// 参数校验异常
			result = Result.error(ResultEnum.PARAM_ERROR.getCode(), ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("，")));
		} else if (e instanceof SaTokenException ex) {
			// Sa-Token的异常
			result = SaTokenExceptionResult.getExceptionResult(ex);
		} else if (e instanceof HttpRequestMethodNotSupportedException ex) {
			// Http请求方式不对
			result = Result.error(ResultEnum.BAD_REQUEST.getCode(), "不支持当前请求方式:" + ex.getMethod());
		} else {
			log.error("服务器异常：{}，请求地址:{}, 具体信息:{}", e.getClass().getName(), ServletUtil.getRequest().getRequestURL(), e.getMessage(), e);
			result = Result.error(ResultEnum.SERVER_ERROR);
		}

		return result;
	}
}
