package sm.system.aop.encrypt;

import java.lang.annotation.*;

/**
 * 接口请求解密注解，标注后请求体 Base64 密文会自动 SM4/CBC 解密为 JSON
 *
 * @author Chekfu
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptApi {
}
