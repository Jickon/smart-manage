package sm.system.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sm.cloud.sys.monitor.job.service.JobExecutionListener;

import javax.sql.DataSource;

/**
 * Quartz 调度器配置
 *
 * @author Chekfu
 */
@Configuration
public class QuartzConfig {

    @Value("${mybatis-flex.datasource.main_db.url}")
    private String url;

    @Value("${mybatis-flex.datasource.main_db.username}")
    private String username;

    @Value("${mybatis-flex.datasource.main_db.password}")
    private String password;

    /**
     * Quartz 专用 DataSource，@QuartzDataSource 让 Spring Boot 自动注入到 SchedulerFactoryBean
     */
    @Bean
    @QuartzDataSource
    DataSource quartzDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaxActive(5);
        ds.setName("QuartzPool");
        return ds;
    }

    /**
     * 注册全局 Job 监听器（setDataSource 由 QuartzAutoConfiguration 自动完成）
     */
    @Bean
    SchedulerFactoryBeanCustomizer quartzCustomizer(JobExecutionListener listener) {
        return factoryBean -> factoryBean.setGlobalJobListeners(listener);
    }
}
