package net.kaciras.blog.infrastructure.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestRedisConfiguration.class)
final class RedisTokenBucketTest {

	private static final String KEY = "TEST";
	private static final String NAMESPACE = "RATE_LIMIT:";

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

		assertThat(limiter.acquire(KEY, 50)).isZero();
		assertThat(limiter.acquire(KEY, 40)).isZero();
		assertThat(limiter.acquire(KEY, 30)).isEqualTo(10);
	}

	@Test
	void restoreSingle() {
		limiter.addBucket(100, 2);

		assertThat(limiter.acquire(KEY, 100)).isZero();

		timePass(50);
		assertThat(limiter.acquire(KEY, 100)).isZero();
		assertThat(limiter.acquire(KEY, 30)).isEqualTo(15);
	}

	@Test
	void noBucket() {
		assertThat(limiter.acquire(KEY, 123456)).isZero();
	}

	@Test
	void overSize() {
		limiter.addBucket(100, 2);
		limiter.addBucket(200, 2);

		assertThat(limiter.acquire(KEY, 150)).isNegative();
	}

	@Test
	void acquireMultiple() {
		limiter.addBucket(40, 4);    // 10  秒内每秒 4 个
		limiter.addBucket(50, 2);    // 100 秒内每秒 2 个
		limiter.addBucket(200, 1);   // 200 秒内每秒 1 个

		assertThat(limiter.acquire(KEY, 40)).isZero();

		timePass(10);
		assertThat(limiter.acquire(KEY, 40)).isEqualTo(5);

		timePass(5);
		assertThat(limiter.acquire(KEY, 40)).isZero();
	}
}
