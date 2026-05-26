package sm.system.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具，实现 ApplicationContextAware 以持有容器引用。
 *
 * @author Chekfu
 */
@Component
public class SpringContextHelper implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextHelper.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalStateException("ApplicationContext未初始化");
		}
		return applicationContext;
	}

	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}

	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}
}
