package sm.framework.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDateTime;

/**
 * Redis配置
 *
 * @author Chekfu
 */
@Configuration
public class RedisConfig {
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);

		// 创建自定义ObjectMapper并配置日期格式化(与JsonConfig保持一致)
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(new LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMAT.getDateTimeFormatter()));
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DatePattern.NORM_DATETIME_FORMAT.getDateTimeFormatter()));
		objectMapper.registerModule(javaTimeModule);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// 启用类型信息保留
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

		// Key 序列化方式
		template.setKeySerializer(new StringRedisSerializer());
		// Value 序列化方式（JSON格式）
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

		// Hash Key 序列化
		template.setHashKeySerializer(new StringRedisSerializer());
		// Hash Value 序列化
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
		// 默认
		template.setDefaultSerializer(new StringRedisSerializer());
		template.afterPropertiesSet();
		return template;
	}
}
