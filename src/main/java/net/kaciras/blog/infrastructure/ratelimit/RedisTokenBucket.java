package net.kaciras.blog.infrastructure.ratelimit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.NonNull;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

/**
 * 令牌桶算法的实现，使用Redis存储相关记录。
 * <p>
 * bucketSize 和 rate 必须大于0，这里没做检查。小于等于0的情况暂时没想到有啥用，以后有需求再看。
 */
public final class RedisTokenBucket implements RateLimiter {

	/** 该类仅作为 Java 语言的接口，算法的实现在 Lua 脚本里，由 Redis 执行 */
	private static final String SCRIPT_FILE = "RateLimiter.lua";

	private final Clock clock;
	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisScript<Long> script;

	private Object[] bArgs = new Object[0];
	private int ttl;
	private int minSize = Integer.MAX_VALUE;

	public RedisTokenBucket(Clock clock, RedisTemplate<String, Object> redisTemplate) {
		this.clock = clock;
		this.redisTemplate = redisTemplate;

		var script = new DefaultRedisScript<Long>();
		script.setResultType(Long.class);
		script.setLocation(new ClassPathResource(SCRIPT_FILE));
		this.script = script;
	}

	public void addBucket(int size, double rate) {
		if (size < 0 || rate <= 0) {
			throw new IllegalArgumentException();
		}
		bArgs = Arrays.copyOf(bArgs, bArgs.length + 2);
		bArgs[bArgs.length - 2] = size;
		bArgs[bArgs.length - 1] = rate;
		ttl = Math.max(ttl, (int) Math.ceil(size / rate));
		minSize = Math.min(minSize, size);
	}

	public long acquire(@NonNull String id, int permits) {
		if (permits == 0) {
			return 0;
		}
		if (permits > minSize) {
			return -1;
		}
		var args = new Object[3 + bArgs.length];
		args[0] = permits;
		args[1] = clock.instant().getEpochSecond();
		args[2] = ttl;
		System.arraycopy(bArgs, 0, args, 3, bArgs.length);

		var waitTime = redisTemplate.execute(script, List.of(id), args);
		if (waitTime == null) {
			throw new RuntimeException("限速脚本返回了空值，ID=" + id);
		}
		return waitTime;
	}
}
