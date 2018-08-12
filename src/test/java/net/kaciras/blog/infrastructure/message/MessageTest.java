package net.kaciras.blog.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kaciras.blog.infrastructure.event.role.RoleEvent;
import net.kaciras.blog.infrastructure.event.role.RoleIncludeChangedEvent;
import net.kaciras.blog.infrastructure.event.role.RoleRemovedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.List;
import java.util.concurrent.CountDownLatch;

class MessageTest {

	private MessageClient client;

	MessageTest() {
		var objectMapper = new ObjectMapper().findAndRegisterModules();
		var s = new FieldMapSerializer(objectMapper);

		var p = new RedisProperties();
		p.setHost("192.168.0.11");
		p.setPort(33671);
		p.setPassword("ZuDNpaB7]vv_iBynJG6ZwPexPPewP]kf");

		var t = new Redis5StreamTransmission(p, s);
		client = new StandardMessageClient(t);
	}

	@Test
	void test() throws InterruptedException {
		var cdl = new CountDownLatch(2);
		client.subscribe(RoleEvent.class, (x) -> {
			cdl.countDown();
		});
		client.send(new RoleRemovedEvent(999));
		client.send(new RoleIncludeChangedEvent(0, List.of(1, 2), List.of(3, 4, 5)));

		cdl.await();
		System.out.println("fin");
	}
}
