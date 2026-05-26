package sm.framework.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sm.framework.json.deserializer.LongJsonDeserializer;
import sm.framework.json.serializer.LongJsonSerializer;

/**
 * JSON 配置
 *
 * @author Chekfu
 */
@Configuration
public class JsonConfig {
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customizer() {
		return builder -> {
			// 日期格式化
			builder.deserializers(new LocalDateDeserializer(DatePattern.NORM_DATE_FORMAT.getDateTimeFormatter()));
			builder.deserializers(new LocalDateTimeDeserializer(DatePattern.NORM_DATETIME_FORMAT.getDateTimeFormatter()));
			builder.serializers(new LocalDateSerializer(DatePattern.NORM_DATE_FORMAT.getDateTimeFormatter()));
			builder.serializers(new LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMAT.getDateTimeFormatter()));
			// Long 转 String，防止js丢失精度
			builder.serializerByType(Long.class, LongJsonSerializer.INSTANCE);
			builder.deserializerByType(Long.class, LongJsonDeserializer.INSTANCE);
		};
	}

}
