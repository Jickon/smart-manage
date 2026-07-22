package sm.domain.sys.base.common.helper;

import cn.dev33.satoken.exception.SaTokenContextException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sm.domain.sys.base.user.model.entity.UserEntity;
import sm.system.helper.CurrentOperatorProvider;

/** 基于当前 Sa-Token 会话提供审计操作人。 */
@Component
@Slf4j
public class SysCurrentOperatorProvider implements CurrentOperatorProvider {

    @Override
    public Long getCurrentUserIdOrNull() {
        try {
            return UserHelper.isLogin() ? UserHelper.getCurrentUserId() : null;
        } catch (SaTokenContextException exception) {
            return null;
        } catch (Exception exception) {
            log.warn("读取当前审计用户失败", exception);
            return null;
        }
    }

    @Override
    public String getCurrentUsernameOrDefault(String defaultUsername) {
        try {
            if (!UserHelper.isLogin()) {
                return defaultUsername;
            }
            UserEntity user = UserHelper.getCurrentUser();
            return user == null || user.getUsername() == null ? defaultUsername : user.getUsername();
        } catch (SaTokenContextException exception) {
            return defaultUsername;
        } catch (Exception exception) {
            log.warn("读取当前审计用户名失败", exception);
            return defaultUsername;
        }
    }
}
