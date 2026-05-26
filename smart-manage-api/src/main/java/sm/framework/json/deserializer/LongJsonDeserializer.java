package sm.framework.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author Chekfu
 */
public class LongJsonDeserializer extends JsonDeserializer<Long> {
	public static final LongJsonDeserializer INSTANCE = new LongJsonDeserializer();

	@Override
	public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		String value = jsonParser.getText();
		try {
			return value == null ? null : Long.parseLong(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
