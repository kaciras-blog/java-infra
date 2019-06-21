package net.kaciras.blog.infrastructure.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import java.time.Duration;

@RequiredArgsConstructor
@Setter
public final class RedisBlockingLimiter implements RateLimiter {

	private final String namespace;
	private final RateLimiter inner;
	private final RedisTemplate<String, Object> redis;

	private Duration banTime = Duration.ofHours(1);
	private boolean refreshOnReject;

	/*
	 * 这里不再使用“异步化”机制，因为并非所有的限流算法都耗时较大，对于个别需要的算法可以在其内部
	 * 自己实现，或是做个装饰器类。
	 */
	@Override
	public long acquire(@NonNull String id, int permits) {
		var blockKey = namespace + id;

		var timeToLive = redis.getExpire(blockKey);
		if (timeToLive == null) {
			throw new IllegalStateException("限流操作不支持Redis管道和事务");
		}
		if (timeToLive > 0) {
			if (refreshOnReject) {
				redis.opsForValue().set(blockKey, "", banTime);
			}
			return timeToLive;
		}

		if (inner.acquire(id, permits) <= 0) {
			return 0;
		}
		redis.opsForValue().set(blockKey, "", banTime);
		return banTime.toSeconds();
	}
}
