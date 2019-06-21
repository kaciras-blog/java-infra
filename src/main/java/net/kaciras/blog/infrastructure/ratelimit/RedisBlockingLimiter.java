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
	 * 【更新】之前版本使用了异步化机制，将对内层限流器的调用和设置封禁记录这两操作放在其他线程中，
	 * 可以减少请求的执行时间。后来移除了，因为并非所有的限流算法都耗时较大，对于个别需要的算法可
	 * 以在其内部自己实现，或是做个异步装饰器类。
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

		var waitTime = inner.acquire(id, permits);
		if (waitTime <= 0) {
			return waitTime;
		}
		redis.opsForValue().set(blockKey, "", banTime);
		return banTime.toSeconds();
	}
}
