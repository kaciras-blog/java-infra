package net.kaciras.blog.infrastructure.ratelimit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import static org.assertj.core.data.Offset.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestRedisConfiguration.class)
final class RedisBlockingLimiterTest {

	private static final String KEY = "TEST";
	private static final String NAMESPACE = "RATE_LIMIT:";

	@Autowired
	private RedisTemplate<String, Object> template;

	@MockBean
	private RateLimiter inner;

	private RedisBlockingLimiter limiter;

	@BeforeEach
	void setUp() {
		limiter = new RedisBlockingLimiter(NAMESPACE, inner, template);
		template.unlink(NAMESPACE + KEY);
	}

	@Test
	void delegateToInner() {
		when(inner.acquire(any(), anyInt())).thenReturn(0L);
		var waitTime = limiter.acquire(KEY, 123);

		verify(inner).acquire(KEY, 123);
		Assertions.assertThat(waitTime).isZero();
	}

	@Test
	void acquireSuccess() {
		when(inner.acquire(any(), anyInt())).thenReturn(0L);
		limiter.acquire(KEY, 123);

		var waitTime = limiter.acquire(KEY, 456);
		Assertions.assertThat(waitTime).isZero();
		verify(inner, times(2)).acquire(any(), anyInt());
	}

	@Test
	void acquireFailed() {
		var banTime = 223344;
		limiter.setBanTime(Duration.ofSeconds(banTime));
		when(inner.acquire(any(), anyInt())).thenReturn(100L);

		var waitTime = limiter.acquire(KEY, 456);
		Assertions.assertThat(waitTime).isEqualTo(banTime);

		waitTime = limiter.acquire(KEY, 1);
		Assertions.assertThat((int) waitTime).isCloseTo(banTime, offset(3));
		verify(inner, times(1)).acquire(any(), anyInt());
	}

	@Test
	void innerReturnsNegative() {
		when(inner.acquire(any(), anyInt())).thenReturn(-1L);

		var waitTime = limiter.acquire(KEY, 1);
		Assertions.assertThat(waitTime).isNegative();

		when(inner.acquire(any(), anyInt())).thenReturn(0L);
		Assertions.assertThat(limiter.acquire(KEY, 1)).isZero();
	}
}
