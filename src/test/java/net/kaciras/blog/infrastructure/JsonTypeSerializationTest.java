package net.kaciras.blog.infrastructure;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kaciras.blog.infrastructure.event.article.ArticleCreatedEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.time.LocalDateTime;

class JsonTypeSerializationTest {

	private final ObjectMapper objectMapper;

	JsonTypeSerializationTest() {
		this.objectMapper = new ObjectMapper();
		var typer = new ObjectMapper
				.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL);
		typer.init(JsonTypeInfo.Id.CLASS, null);
		typer.inclusion(JsonTypeInfo.As.PROPERTY);
		typer.typeProperty(":class");

		objectMapper.setDefaultTyping(typer);
		objectMapper.findAndRegisterModules();
	}

	@Test
	void testJackson() {
		var s = new GenericJackson2JsonRedisSerializer(objectMapper);

		var e = new ArticleCreatedEvent(22, 333, 100);
		var bytes = s.serialize(e);
//		System.out.println(new String(bytes));

		var dec = s.deserialize(bytes);
		Assertions.assertThat(dec).isEqualToComparingFieldByFieldRecursively(e);
	}

	@Test
	void testNumber() {
		var s = new GenericJackson2JsonRedisSerializer(objectMapper);
		var bytes = s.serialize(123456);
		var des = s.deserialize(bytes);
		Assertions.assertThat(des).isInstanceOf(Integer.class);
	}

	/**
	 * 因为字符类型在JSON中不存在，所以会序列化成字符串类型；时间类型具有自定
	 * 义的序列化格式，该格式不带类型信息所以无法读取。
	 * <p>
	 * 该测试说明对一些常用类型的序列化并未按照对象的方式，而是有特定的规则，
	 * 这些规则在各种语言中是不同的，没法简单地跨平台。
	 */
	@Test
	void testPlatformRelated() {
		var s = new GenericJackson2JsonRedisSerializer(objectMapper);
		var charEncode = s.serialize('C');
		Assertions.assertThat(s.deserialize(charEncode)).isInstanceOf(String.class);

		var timeEncode = s.serialize(LocalDateTime.now());
		Assertions.assertThatThrownBy(() -> s.deserialize(timeEncode))
				.isInstanceOf(SerializationException.class);
	}
}
