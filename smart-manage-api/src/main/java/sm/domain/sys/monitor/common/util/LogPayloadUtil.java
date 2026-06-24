package sm.domain.sys.monitor.common.util;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 日志正文截断与敏感字段脱敏
 *
 * @author Chekfu
 */
public final class LogPayloadUtil {
	private static final int DEFAULT_MAX = 4000;
	private static final Set<String> SENSITIVE_KEYS = Set.of(
			"password", "oldpassword", "newpassword", "captcha", "token", "authorization", "smtoken"
	);
	private static final Pattern KEY_IN_JSON = Pattern.compile(
			"\"(password|token|authorization|smtoken|Captcha|captcha)\"\\s*:\\s*\"([^\"]*)\"");

	private LogPayloadUtil() {
	}

	public static String truncate(String s, int max) {
		if (s == null) {
			return null;
		}
		int m = max > 0 ? max : DEFAULT_MAX;
		if (s.length() <= m) {
			return s;
		}
		return s.substring(0, m) + "...(truncated)";
	}

	public static String maskJsonLike(String json) {
		if (json == null || json.isEmpty()) {
			return json;
		}
		return KEY_IN_JSON.matcher(json).replaceAll(mr -> {
			String key = mr.group(1);
			return "\"" + key + "\":\"***\"";
		});
	}

	public static String maskNameLike(String name) {
		if (name == null) {
			return null;
		}
		return name.toLowerCase(Locale.ROOT).contains("password")
				|| name.toLowerCase(Locale.ROOT).contains("token")
				? "***" : name;
	}

	public static boolean isSensitiveKey(String key) {
		if (key == null) {
			return false;
		}
		return SENSITIVE_KEYS.contains(key.toLowerCase(Locale.ROOT));
	}
}

