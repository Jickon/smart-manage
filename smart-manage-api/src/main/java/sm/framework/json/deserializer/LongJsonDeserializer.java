package sm.framework.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * @author Chekfu
 */
public class LongJsonDeserializer extends JsonDeserializer<Long> {
	public static final LongJsonDeserializer INSTANCE = new LongJsonDeserializer();

	@Override
	public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		String value = jsonParser.getText();
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException exception) {
			// 基础设施层不能静默吞掉非法 ID，否则业务层会误判为“未传值”。
			throw JsonMappingException.from(jsonParser, "Long 字段必须是数字字符串：" + value, exception);
		}
	}
}
