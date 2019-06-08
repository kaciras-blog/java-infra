package net.kaciras.blog.infrastructure.ratelimit;

import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Clock;
import java.util.List;

/**
 * 令牌桶算法的实现，使用Redis存储相关记录。
 *
 * bucketSize 和 rate 必须大于0，这里没做检查。小于等于0的情况暂时没想到有啥用，以后有需求再看。
 */
@Setter
public final class RedisTokenBucket implements RateLimiter {

	/** 该类仅作为 Java 语言的接口，算法的实现在 Lua 脚本里，由 Redis 执行 */
	private static final String SCRIPT_FILE = "RateLimiter.lua";

	private final Clock clock;
	private final RedisTemplate<String, Object> redisTemplate;

	private final RedisScript<Long> script;

	private int bucketSize;
	private double rate;

	public RedisTokenBucket(Clock clock, RedisTemplate<String, Object> redisTemplate) {
		this.clock = clock;
		this.redisTemplate = redisTemplate;

		var script = new DefaultRedisScript<Long>();
		script.setResultType(Long.class);
		script.setLocation(new ClassPathResource(SCRIPT_FILE));
		this.script = script;
	}

	/**
	 * 获取指定数量的令牌，返回桶内拥有足够令牌所需要等待的时间。
	 *
	 * @param id      标识获取者的身份，一般是对方的IP之类的
	 * @param permits 要获取的令牌数量
	 * @return 需要等待的时间（秒），0表示成功，小于0表示永远无法完成
	 */
	public long acquire(String id, int permits) {
		if (permits > bucketSize) {
			return -1;
		}
		// TODO: ttl 移入lua里计算，怎么统一时间单位
		var now = clock.instant().getEpochSecond();
		var ttl = bucketSize / rate;
		var waitTime = redisTemplate.execute(script, List.of(id), permits, now, bucketSize, rate, ttl);

		if (waitTime == null) {
			throw new RuntimeException("限速脚本返回了空值，ID=" + id);
		}
		return waitTime;
	}
}
