package net.kaciras.blog.infrastructure.ratelimit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;

@ActiveProfiles("local")
@SpringBootTest(classes = RedisRateLimiterTest.TestConfiguration.class)
final class RedisRateLimiterTest {

	private static final String KEY = "RATE_LIMITER_TEST";

	// 使用内嵌的配置，避免加载整个应用
	@Import(RedisAutoConfiguration.class)
	@Configuration
	static class TestConfiguration {

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

	private Clock clock;

	@BeforeEach
	void setUp() {
		clock = Mockito.mock(Clock.class);
		Mockito.when(clock.instant()).thenReturn(Instant.ofEpochSecond(0));
		template.unlink(KEY);
	}

	@Test
	void acquireSingle() {
		var limiter = new RedisTokenBucket(clock, template);
		limiter.addBucket(100, 2);

		Assertions.assertThat(limiter.acquire(KEY, 50)).isZero();
		Assertions.assertThat(limiter.acquire(KEY, 40)).isZero();

		Assertions.assertThat(limiter.acquire(KEY, 40)).isEqualTo(15);
	}

	@Test
	void restoreSingle() {
		var limiter = new RedisTokenBucket(clock, template);
		limiter.addBucket(100, 2);

		Assertions.assertThat(limiter.acquire(KEY, 100)).isZero();

		Mockito.when(clock.instant()).thenReturn(Instant.ofEpochSecond(50));
		Assertions.assertThat(limiter.acquire(KEY, 100)).isZero();
		Assertions.assertThat(limiter.acquire(KEY, 30)).isEqualTo(15);
	}

	@Test
	void acquireOverLimit() {
		var limiter = new RedisTokenBucket(clock, template);
		limiter.addBucket(100, 2);
		limiter.addBucket(200, 2);

		Assertions.assertThat(limiter.acquire(KEY, 150)).isNegative();
	}

	@Test
	void acquireMultiple() {
		var limiter = new RedisTokenBucket(clock, template);
		limiter.addBucket(40, 4);    // 10  秒内每秒 4 个
		limiter.addBucket(50, 2);    // 100 秒内每秒 2 个
		limiter.addBucket(200, 1);    // 200 秒内每秒 1 个

		Assertions.assertThat(limiter.acquire(KEY, 40)).isZero();

		Mockito.when(clock.instant()).thenReturn(Instant.ofEpochSecond(10));
		Assertions.assertThat(limiter.acquire(KEY, 40)).isEqualTo(5);

		Mockito.when(clock.instant()).thenReturn(Instant.ofEpochSecond(15));
		Assertions.assertThat(limiter.acquire(KEY, 40)).isZero();
	}
}
