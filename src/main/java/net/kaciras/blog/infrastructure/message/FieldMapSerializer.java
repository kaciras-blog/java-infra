package net.kaciras.blog.infrastructure.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 以JSON格式来序列化对象的字段，序列化后的数据为字符串数组，其中奇数项为
 * 字段名，偶数项为字段的值，第一对为被序列化对象的类型（以class为字段名）。
 *
 * 该序列化类能够处理复合对象和继承的属性，内部对象直接被序列化为一个
 * Object的JSON类型。
 */
@RequiredArgsConstructor
public class FieldMapSerializer {

	private final ObjectMapper objectMapper;

	private final Map<Class, Field[]> cache = new ConcurrentHashMap<>();

	public Object[] serialize(Object object) throws IllegalAccessException, JsonProcessingException {
		var clazz = object.getClass();
		var fields = cache.computeIfAbsent(clazz, this::buildCache);

		var result = new Object[(fields.length << 1) + 2];
		result[0] = "class";
		result[1] = clazz.getName();

		for (int i = 0; i < fields.length; i++) {
			result[(i << 1) + 2] = fields[i].getName();
			result[(i << 1) + 3] = objectMapper.writeValueAsString(fields[i].get(object));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T deserialize(Map<String, String> attrs) throws ReflectiveOperationException, IOException {
		var clazz = Class.forName(attrs.get("class"));
		var fields = cache.computeIfAbsent(clazz, this::buildCache);

		var object = clazz.getConstructor().newInstance();
		for (var field : fields) {
			field.set(object, objectMapper.readValue(attrs.get(field.getName()), field.getType()));
		}
		return (T) object;
	}

	/**
	 * 缓存下对象的字段，下次序列化同类型对象时无需再递归查询继承的字段。
	 *
	 * @param clazz 被序列化对象的类型
	 * @return 对象的所有字段
	 */
	private Field[] buildCache(Class clazz) {
		var flux = Flux.<Field>empty();

		while (clazz != Object.class) {
			flux = flux.mergeWith(Flux.fromArray(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		return flux.doOnNext(f -> f.setAccessible(true))
				.collectList()
				.map(list -> list.toArray(new Field[0])).block();
	}
}
