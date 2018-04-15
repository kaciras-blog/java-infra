package net.kaciras.blog.infrastructure.message;

import net.kaciras.blog.infrastructure.event.article.ArticleCreatedEvent;
import net.kaciras.blog.infrastructure.event.discussion.DiscussCreatedEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class JacksonJsonCodecTest {

	private static JacksonJsonCodec codec;

	@BeforeAll
	static void setUpClass() {
		codec = new JacksonJsonCodec();
		codec.registerEvents("net.kaciras.blog.infrastructure.event");
	}

	@Test
	void test() throws Exception {
		DomainEvent event = new DiscussCreatedEvent(1, 3, 7, 11);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		codec.serialize(outputStream, event);

		Object o = codec.deserialize(new ByteArrayInputStream(outputStream.toByteArray()));
		Assertions.assertThat(o).isEqualToComparingFieldByField(event);
	}
}
