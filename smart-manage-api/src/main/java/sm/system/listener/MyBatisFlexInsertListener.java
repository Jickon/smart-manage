package sm.system.listener;

import com.mybatisflex.annotation.InsertListener;
import sm.cloud.sys.common.helper.UserHelper;
import sm.system.entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * MyBatis-Flex 插入监听，自动填充 BaseEntity 审计字段。
 * 异步线程无 Sa-Token 上下文时降级，不抛异常。
 *
 * @author Chekfu
 */
public class MyBatisFlexInsertListener implements InsertListener {

	@Override
	public void onInsert(Object o) {
		if (o instanceof BaseEntity entity) {
			entity.setCreateTime(LocalDateTime.now());
			entity.setCreateUser(getUserIdSafe());
		}
	}

	private static Long getUserIdSafe() {
		try {
			return UserHelper.getCurrentUserId();
		} catch (Exception ignored) {
			return null;
		}
	}
}
