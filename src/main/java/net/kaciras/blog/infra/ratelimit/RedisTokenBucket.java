package net.kaciras.blog.infra.ratelimit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.NonNull;

import java.time.Clock;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * 令牌桶算法的实现，使用Redis存储相关记录，该类里可以包含多个令牌桶。
 * <p>
 * TODO: Spring Data Redis 里的 ScriptExecutor 跟 RedisTemplate 绑死了，很难直接基于 Connection 实现
 */
public final class RedisTokenBucket implements RateLimiter {

	/** 该类仅作为 Java 语言的接口，算法的实现在 Lua 脚本里，由 Redis 执行 */
	private static final String SCRIPT_FILE = "TokenBucket.lua";

	/*
	 * 经过一番思考，还是决定将命名空间放在限流器对象里，理由如下：
	 *   1.命名空间应当看作限流器的一部分，用于标识键的类型以跟其他数据隔离
	 *   2.如果需要进一步区分，则可以在id参数上做修改
	 *   3.如果使用装饰模式来扩展，则必须要在实例里对命名空间做区分
	 *   4.对于其他限流算法如简单计数等，不存在命名空间，要求在调用方对id做处理是多余的
	 */
	private final String namespace;
	private final Clock clock;
	private final RedisTemplate<String, Object> redis;

	private final RedisScript<Long> script;

	private Object[] bArgs = new Object[0];

	/** 记录在Redis里的过期时间，该值是由容量和速率来计算的 */
	private int ttl;

	/** 最小的一个桶的容量 */
	private int minSize = Integer.MAX_VALUE;

	public RedisTokenBucket(String namespace, RedisTemplate<String, Object> redis, Clock clock) {
		this.namespace = namespace;
		this.redis = redis;
		this.clock = clock;

		var script = new DefaultRedisScript<Long>();
		script.setResultType(Long.class);
		script.setLocation(new ClassPathResource(SCRIPT_FILE));
		this.script = script;
	}

	/**
	 * 添加一个令牌桶，该桶具有指定的容量和填充速率。
	 *
	 * @param size 桶容量
	 * @param rate 填充速率（令牌/秒）
	 */
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

	/**
	 * 遍历所有的令牌桶，从每个桶中都要取走指定数量的令牌。
	 * <p>
	 * 当所有令牌桶内都有充足的令牌时返回0，否则返回需要等待的时间。
	 * 如果多个令牌桶的令牌都不足，则返回等待时间最长的。
	 * 只要返回非零值，则所有令牌桶都不会被修改，本次请求不造成任何影响。
	 * <p>
	 * permits 小于等于0的情况没有处理，调用方自己考虑其意义
	 */
	public long acquire(@NonNull String id, int permits) {
		if (permits > minSize) {
			return -1;
		}
		var keys = Collections.singletonList(namespace + Objects.requireNonNull(id));

		var args = new Object[3 + bArgs.length];
		args[0] = permits;
		args[1] = clock.instant().getEpochSecond();
		args[2] = ttl;
		System.arraycopy(bArgs, 0, args, 3, bArgs.length);

		var waitTime = redis.execute(script, keys, args);
		if (waitTime == null) {
			throw new RuntimeException("限速脚本返回了空值，ID=" + id);
		}
		return waitTime;
	}
}