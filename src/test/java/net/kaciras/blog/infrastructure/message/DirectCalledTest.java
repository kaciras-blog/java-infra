package net.kaciras.blog.infrastructure.message;

import net.kaciras.blog.infrastructure.event.DomainEvent;
import net.kaciras.blog.infrastructure.event.article.ArticleCreatedEvent;
import net.kaciras.blog.infrastructure.event.discussion.DiscussCreatedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class DirectCalledTest {

	private DirectMessageClient client = new DirectMessageClient();

	@Test
	void test() {
		var value = new AtomicInteger();

		client.getChannel(DomainEvent.class).subscribe(e -> value.incrementAndGet());
		client.getChannel(ArticleCreatedEvent.class).subscribe(e -> value.incrementAndGet());

		client.send(new DiscussCreatedEvent());
		Assertions.assertEquals(1, value.get());

		value.set(0);
		client.getChannel(DomainEvent.class).subscribe(e -> value.incrementAndGet());
		client.send(new ArticleCreatedEvent());
		Assertions.assertEquals(3, value.get());
	}
}
