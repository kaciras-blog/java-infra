package net.kaciras.blog.infrastructure.message;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.kaciras.blog.infrastructure.event.discussion.DiscussCreatedEvent;
import net.kaciras.blog.infrastructure.event.role.RoleRemovedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class PubSubTest {

	@BeforeAll
	static void configLog() {
		Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.setLevel(Level.OFF);
	}

	@Test
	void testLinkedQueue() {
		testPubSub(new QueueTransmission());
//		testCallback(new LinkedEventQueue<>());
	}

//	@Test
//	void testRedisQueue() {
//		RedisClient client = RedisClient.create(RedisURI.Builder.redis("123.206.206.29")
//				.withPassword("39ow0OO=i:P)(:TqKz)P@8ZVq=l&.w=X[i:OhO{FK\\h^>B>}&!}Z(>f9*6`htLS\\")
//				.withPort(33671).build());
//		client.setOptions(ClientOptions.builder()
//				.autoReconnect(true)
//				.socketOptions(SocketOptions.builder()
//						.connectTimeout(Duration.ofSeconds(3)).build())
//				.build());
//		try (RedisEventQueue<DomainEvent> queue = new RedisEventQueue<>(client)) { testPubSub(queue); }
//	}

	private void testPubSub(Transmission queue) {
		Assertions.assertTimeout(Duration.ofSeconds(8), () ->{
			MessageClient messager = new StandardMessageClient(queue, Executors.newSingleThreadExecutor());

			AtomicInteger counter = new AtomicInteger();
			CountDownLatch cdl = new CountDownLatch(3);

			messager.subscribe(DomainEvent.class, e -> cdl.countDown());
			messager.subscribe(DiscussCreatedEvent.class, e -> counter.incrementAndGet());
			messager.subscribe(RoleRemovedEvent.class, e -> counter.incrementAndGet());

			messager.send(new DiscussCreatedEvent());
			messager.send(new DiscussCreatedEvent());
			messager.send(new DiscussCreatedEvent());

			cdl.await();
			Assertions.assertEquals(3, counter.get());
		});
	}

//	private void testCallback(Transmission queue) {
//		Assertions.assertTimeout(Duration.ofSeconds(8), () -> {
//			MessageClient messager = new StandardMessageClient(queue, Executors.newSingleThreadExecutor());
//			CountDownLatch cdl = new CountDownLatch(2);
//
//			messager.subscribe(DiscussCreatedEvent.class, e -> cdl.countDown());
//			messager.send(new DiscussCreatedEvent()).subscribe(r -> cdl.countDown());
//			cdl.await();
//		});
//	}

	@Test
	void test() throws Exception {
		Cache<Object, Object> build = CacheBuilder.newBuilder()
				.expireAfterWrite(2, TimeUnit.SECONDS)
				.build();
		ConcurrentMap<Object, Object> map = build.asMap();
		map.put(1, 2);
		Thread.sleep(2200);
		map.put(8, 2);
		map.put(12, 2);
		map.put(55, 2);


		Assertions.assertEquals(2, map.size());
	}
}
