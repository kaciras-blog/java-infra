package net.kaciras.blog.infrastructure.typesession;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.Map;


public class RegisterJacksonSerializer implements RedisSerializer<Object> {

	private final ObjectMapper objectMapper;

	/**
	 * 顶层类型是一些
	 */
	private final Map<Class, Integer> primitiveId = Map.of(
			Byte.class, 0x80,
			Short.class, 0x81,
			Integer.class, 0x82,
			Long.class, 0x83,
			Float.class, 0x84,
			Double.class, 0x85,
			Character.class, 0x86
			// Enum: 0x87
	);

	public RegisterJacksonSerializer(ObjectMapper objectMapper) {
		objectMapper = objectMapper.copy();

		var typer = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL);
		typer.inclusion(JsonTypeInfo.As.PROPERTY);
		typer.typeProperty(":class");
		typer.init(JsonTypeInfo.Id.CLASS, new RegisterTypeIdResolver());

		this.objectMapper= objectMapper.setDefaultTyping(typer);
	}

	@Override
	public byte[] serialize(Object o) throws SerializationException {

		return new byte[0];
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		return objectMapper;
	}
}
