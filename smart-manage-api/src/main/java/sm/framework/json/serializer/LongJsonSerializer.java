package sm.framework.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Long 转 String，防止js丢失精度
 *
 * @author Chekfu
 */
public class LongJsonSerializer extends JsonSerializer<Long> {
	public static final LongJsonSerializer INSTANCE = new LongJsonSerializer();

	@Override
	public void serialize(Long value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
		if (null == value) {
			gen.writeNull();
			return;
		}
		// 统一序列化为字符串：避免前端出现 number/string 混用，并彻底规避 JS 精度丢失
		gen.writeString(String.valueOf(value));
	}
}
