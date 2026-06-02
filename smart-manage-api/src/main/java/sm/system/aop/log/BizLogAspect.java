package sm.system.aop.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sm.cloud.sys.common.helper.UserHelper;
import sm.cloud.sys.monitor.common.util.LogPayloadUtil;
import sm.system.util.ServletUtil;

import java.lang.reflect.Method;

/**
 * 业务操作日志 AOP
 *
 * @author Chekfu
 */
@Aspect
@Order(100)
@Component
@Slf4j
@RequiredArgsConstructor
public class BizLogAspect {
    private final ObjectMapper objectMapper;
    private final OperateLogWriter operateLogWriter;

    @Pointcut("@annotation(sm.system.aop.log.BizLog)")
    private void getLogPointCut() {
    }

    @Around("getLogPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        BizLog bizLog = method.getAnnotation(BizLog.class);
        long t0 = System.currentTimeMillis();

        String ip = null;
        String userAgent = null;
        String requestUri = null;
        String requestMethod = null;
        try {
            var req = ServletUtil.getRequest();
            ip = ServletUtil.getClientIp();
            userAgent = req.getHeader("User-Agent");
            requestUri = req.getRequestURI();
            requestMethod = req.getMethod();
        } catch (Exception e) {
            log.warn("获取请求元数据失败", e);
        }

        Long userId = null;
        String username = "未知";
        try {
            if (UserHelper.isLogin()) {
                userId = UserHelper.getCurrentUserId();
                var u = UserHelper.getCurrentUser();
                if (u != null && u.getUsername() != null) {
                    username = u.getUsername();
                }
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败", e);
        }

        String params = null;
        if (bizLog.saveRequest()) {
            params = LogPayloadUtil.truncate(LogPayloadUtil.maskJsonLike(serializeArgs(joinPoint.getArgs())), bizLog.maxParamLen());
        }

        String err = null;
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            err = e.getMessage() != null ? LogPayloadUtil.truncate(e.getMessage(), 2000) : e.getClass().getSimpleName();
            long ms = System.currentTimeMillis() - t0;
            writeLog(bizLog, joinPoint, false, err, ip, userAgent, requestUri, requestMethod, userId, username, params, null, ms);
            throw e;
        }
        long duration = System.currentTimeMillis() - t0;
        String body = null;
        if (bizLog.saveResponse()) {
            body = LogPayloadUtil.truncate(LogPayloadUtil.maskJsonLike(serializeObject(result)), bizLog.maxResponseLen());
        }
        writeLog(bizLog, joinPoint, true, null, ip, userAgent, requestUri, requestMethod, userId, username, params, body, duration);
        return result;
    }

    private void writeLog(BizLog bizLog, JoinPoint joinPoint, boolean success, String errorMsg,
                          String ip, String userAgent, String requestUri, String requestMethod,
                          Long userId, String username, String requestParams, String responseBody, long durationMs) {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        OperateLogPayload payload = new OperateLogPayload(
            bizLog.value(),
            success,
            errorMsg,
            requestMethod,
            requestUri,
            ip,
            userAgent,
            sig.getDeclaringTypeName(),
            sig.getName(),
            durationMs,
            requestParams,
            responseBody,
            userId,
            username
        );
        operateLogWriter.write(payload);
    }

    private String serializeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException ex) {
            return "[unserializable args]";
        }
    }

    private String serializeObject(Object o) {
        if (o == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return o.toString();
        }
    }
}
