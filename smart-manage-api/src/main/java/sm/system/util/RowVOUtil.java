package sm.system.util;

import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.MapUtil;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chekfu
 */
public class RowVOUtil {
	static final String INDEX_SEPARATOR = "$";
	private static final Map<Class<?>, Map<String, Method>> CLASS_SETTERS_CACHE = new ConcurrentHashMap<>();

	private RowVOUtil() {
	}

	public static <T> T toVO(Row row, Class<T> objectClass) {
		return toVO(row, objectClass, 0);
	}


	public static <T> T toVO(Row row, Class<T> objectClass, int index) {
		T instance = ClassUtil.newInstance(objectClass);
		Map<String, Method> classSetters = getSetterMethods(objectClass);
		Set<String> rowKeys = row.keySet();
		classSetters.forEach((property, setter) -> {
			try {
				if (index <= 0) {
					for (String rowKey : rowKeys) {
						if (property.equalsIgnoreCase(rowKey)) {
							Object rowValue = row.get(rowKey);
							Object value = ConvertUtil.convert(rowValue, setter.getParameterTypes()[0], true);
							setter.invoke(instance, value);
						}
					}
				} else {
					for (int i = index; i >= 0; i--) {
						String newProperty = i <= 0 ? property : property + INDEX_SEPARATOR + i;
						boolean fillValue = false;
						for (String rowKey : rowKeys) {
							if (newProperty.equalsIgnoreCase(rowKey)) {
								Object rowValue = row.get(rowKey);
								Object value = ConvertUtil.convert(rowValue, setter.getParameterTypes()[0], true);
								setter.invoke(instance, value);
								fillValue = true;
								break;
							}
						}
						if (fillValue) {
							break;
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Can not invoke method: " + setter);
			}
		});
		return instance;
	}


	public static <T> List<T> toVOList(List<Row> rows, Class<T> objectClass) {
		return toVOList(rows, objectClass, 0);
	}

	public static <T> List<T> toVOList(List<Row> rows, Class<T> objectClass, int index) {
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<T> objectList = new ArrayList<>();
			for (Row row : rows) {
				objectList.add(toVO(row, objectClass, index));
			}
			return objectList;
		}
	}


	private static Map<String, Method> getSetterMethods(Class<?> aClass) {
		return MapUtil.computeIfAbsent(CLASS_SETTERS_CACHE, aClass, aClass1 -> {
			Map<String, Method> columnSetterMapping = new HashMap<>();
			List<Method> setters = ClassUtil.getAllMethods(aClass1,
					method -> method.getName().startsWith("set")
							&& method.getParameterCount() == 1
							&& Modifier.isPublic(method.getModifiers())
			);
			for (Method setter : setters) {
				String column = setter.getName().substring(3);
				columnSetterMapping.put(column, setter);
				columnSetterMapping.put(StringUtil.camelToUnderline(column), setter);
				columnSetterMapping.put(StringUtil.underlineToCamel(column), setter);
			}
			return columnSetterMapping;
		});
	}
}
