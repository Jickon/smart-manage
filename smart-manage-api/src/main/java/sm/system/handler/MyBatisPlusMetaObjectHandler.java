package sm.system.handler;

import cn.dev33.satoken.exception.SaTokenContextException;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * 统一填充实体审计字段。
 *
 * @author Chekfu
 */
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 仅对 BaseEntity 子类填充审计字段，避免误影响其他实体。
        if (!(metaObject.getOriginalObject() instanceof BaseEntity)) {
            return;
        }
        setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        setFieldValByName("createUser", currentUserId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (!(metaObject.getOriginalObject() instanceof BaseEntity)) {
            return;
        }
        // 更新操作必须覆盖旧审计值，不能使用仅在字段为空时生效的 strictUpdateFill。
        setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        setFieldValByName("updateUser", currentUserId(), metaObject);
    }

    private Long currentUserId() {
        // 异步日志和 Quartz 线程没有 Sa-Token 上下文，审计用户按约定留空。
        try {
            return StpUtil.isLogin() ? UserHelper.getCurrentUserId() : null;
        } catch (SaTokenContextException e) {
            return null;
        } catch (Exception e) {
            // 非预期异常兜底，不中断持久化流程。
            return null;
        }
    }
}
