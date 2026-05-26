package sm.framework.config;

import com.mybatisflex.core.FlexGlobalConfig;
import org.springframework.context.annotation.Configuration;
import sm.system.entity.BaseEntity;
import sm.system.listener.MyBatisFlexInsertListener;
import sm.system.listener.MyBatisFlexUpdateListener;

/**
 * MybatisFlex配置类
 *
 * @author Chekfu
 */
@Configuration
public class MyBatisFlexConfiguration {

	/**
	 * 设置MybatisFlex插入、更新监听器
	 */
	public MyBatisFlexConfiguration() {
		MyBatisFlexInsertListener mybatisInsertListener = new MyBatisFlexInsertListener();
		MyBatisFlexUpdateListener mybatisUpdateListener = new MyBatisFlexUpdateListener();
		FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();

		//设置BaseEntity类启用
		config.registerInsertListener(mybatisInsertListener, BaseEntity.class);
		config.registerUpdateListener(mybatisUpdateListener, BaseEntity.class);
	}
}
