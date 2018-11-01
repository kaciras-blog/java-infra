package net.kaciras.blog.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kaciras.blog.infrastructure.typesession.JacksonDefiniteSerializer;
import net.kaciras.blog.infrastructure.typesession.TypeWrapperRedisSerializer;
import org.junit.jupiter.api.Test;

class TypeSessionTest {

	@Test
	void test() {
		var objectMapper = new ObjectMapper();
		var s = new TypeWrapperRedisSerializer(new JacksonDefiniteSerializer(objectMapper));

		s.registerClass("I", Integer.class);
		var bytes = s.serialize(123);
		System.out.println(new String(bytes));
	}
}
