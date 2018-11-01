package net.kaciras.blog.infrastructure.typesession;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TypeWrapperRedisSerializer implements RedisSerializer<Object> {

	private final Map<String, Class<?>> typeRegistration = new HashMap<>();
	private final Map<Class<?>, String> reversedMap = new HashMap<>();

	private final DefiniteSerializer inner;

	public TypeWrapperRedisSerializer(DefiniteSerializer inner) {
		this.inner = inner;
		registerClass("I", Integer.class);
		registerClass("L", Long.class);
//		registerClass("S")
	}

	public TypeWrapperRedisSerializer registerClass(String name, Class type) {
		typeRegistration.put(name, type);
		reversedMap.put(type, name);
		return this;
	}

	@Override
	public byte[] serialize(Object value) throws SerializationException {
		var out = new ByteArrayOutputStream();
		var clazz = value.getClass();
		// clazz.isPrimitive();

		try {
			out.write(reversedMap.get(clazz).getBytes(StandardCharsets.UTF_8));
			out.write(';');
		} catch (IOException e) {
			throw new AssertionError("ByteArrayOutputStream never throws IOException.");
		}

		inner.serialize(value, out);
		return out.toByteArray();
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		var separate = 0;
		while (separate < bytes.length && bytes[separate] != ';') {
			separate++;
		}
		var type = typeRegistration.get(new String(bytes, 0, separate, StandardCharsets.UTF_8));
		return inner.deserialize(new ByteArrayInputStream(bytes, separate, bytes.length), type);
	}
}
