package sm.system.aop.encrypt;

import java.lang.annotation.*;

/**
 * 接口响应加密注解，标注后响应 JSON 会整体 SM4/CBC 加密并以 Base64 字符串返回
 *
 * @author Chekfu
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptApi {
}
