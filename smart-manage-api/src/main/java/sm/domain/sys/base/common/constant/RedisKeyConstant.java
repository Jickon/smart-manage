package sm.domain.sys.base.common.constant;

/**
 * @author Chekfu
 */
public class RedisKeyConstant {
	// Redis Key 前缀
	public static final String YUN = "sys:";
	public static final String BASE = "base:";
	public static final String CAPTCHA = YUN + BASE + "captcha:";

	// JetCache 缓存名称（格式：领域:应用:key名称）
	public static final String CACHE_BASIC_DATA_ITEMS = YUN + BASE + "basic-data-items";
}
