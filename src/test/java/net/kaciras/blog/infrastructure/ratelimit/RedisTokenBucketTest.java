package net.kaciras.blog.infrastructure.ratelimit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Clock;
import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RedisTokenBucketTest.EmbeddedConfiguration.class)
final class RedisTokenBucketTest {

	private static final String KEY = "TEST";
	private static final String NAMESPACE = "RATE_LIMIT:";

	// 使用内嵌的配置，避免加载整个应用
	@Import(RedisAutoConfiguration.class)
	@Configuration
	static class EmbeddedConfiguration {

		@Bean
		RedisTemplate<String, Object> testRedisTemplate(RedisConnectionFactory factory) {
			var redisTemplate = new RedisTemplate<String, Object>();
			redisTemplate.setConnectionFactory(factory);
			redisTemplate.setEnableDefaultSerializer(false);
			redisTemplate.setKeySerializer(RedisSerializer.string());
			redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
			return redisTemplate;
		}
	}

	@Autowired
	private RedisTemplate<String, Object> template;

	private Clock clock = mock(Clock.class);
	private int timeSecond;

	private RedisTokenBucket limiter;

	@BeforeEach
	void setUp() {
		limiter = new RedisTokenBucket(NAMESPACE, template, clock);
		template.unlink(NAMESPACE + KEY);
		when(clock.instant()).thenReturn(Instant.ofEpochSecond(timeSecond));
	}

	private void timePass(int second) {
		timeSecond += second;
		when(clock.instant()).thenReturn(Instant.ofEpochSecond(timeSecond));
	}

	@Test
	void acquireSingle() {
		limiter.addBucket(100, 2);

		Assertions.assertThat(limiter.acquire(KEY, 50)).isZero();
		Assertions.assertThat(limiter.acquire(KEY, 40)).isZero();
		Assertions.assertThat(limiter.acquire(KEY, 30)).isEqualTo(10);
	}

	@Test
	void restoreSingle() {
		limiter.addBucket(100, 2);

		Assertions.assertThat(limiter.acquire(KEY, 100)).isZero();

		timePass(50);
		Assertions.assertThat(limiter.acquire(KEY, 100)).isZero();
		Assertions.assertThat(limiter.acquire(KEY, 30)).isEqualTo(15);
	}

	@Test
	void noBucket() {
		Assertions.assertThat(limiter.acquire(KEY, 123456)).isZero();
	}

	@Test
	void overSize() {
		limiter.addBucket(100, 2);
		limiter.addBucket(200, 2);

		Assertions.assertThat(limiter.acquire(KEY, 150)).isNegative();
	}

	@Test
	void acquireMultiple() {
		limiter.addBucket(40, 4);    // 10  秒内每秒 4 个
		limiter.addBucket(50, 2);    // 100 秒内每秒 2 个
		limiter.addBucket(200, 1);   // 200 秒内每秒 1 个

		Assertions.assertThat(limiter.acquire(KEY, 40)).isZero();

		timePass(10);
		Assertions.assertThat(limiter.acquire(KEY, 40)).isEqualTo(5);

		timePass(5);
		Assertions.assertThat(limiter.acquire(KEY, 40)).isZero();
	}
}
