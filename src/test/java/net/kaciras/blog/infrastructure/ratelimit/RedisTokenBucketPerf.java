package net.kaciras.blog.infrastructure.ratelimit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

/**
 * 测量 RedisTokenBucket 的性能，其 acquire 方法包括三个方面的开销：JAVA层逻辑、通信开销、Redis脚本执行时间。
 * 如果单独衡量 TokenBucket.lua 脚本的性能，请使用 redis-benchmark 来测，结果见 resource/TokenBucketBenchmark.txt
 */
@SuppressWarnings({"UnusedReturnValue", "unchecked"})
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Measurement(iterations = 5, time = 10)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RedisTokenBucketPerf {

	private static final String NAMESPACE = "TokenBucket:";
	private static final String KEY = "Benchmark";

	private ConfigurableApplicationContext context;

	private RedisTokenBucket single;
	private RedisTokenBucket twenty;

	@Setup
	public void setUp() {
		context = new SpringApplicationBuilder()
				.sources(RedisTokenBucketTest.EmbeddedConfiguration.class)
				.web(WebApplicationType.NONE).run();
	}

	@Setup(Level.Iteration)
	public void setUpIteration() {
		var template = (RedisTemplate<String, Object>) context.getBean("testRedisTemplate");
		template.unlink(KEY);

		single = new RedisTokenBucket(NAMESPACE, template, Clock.systemDefaultZone());
		single.addBucket(Integer.MAX_VALUE, 10_0000);

		twenty = new RedisTokenBucket(NAMESPACE, template, Clock.systemDefaultZone());
		for (int i = 0; i < 20; i++) {
			twenty.addBucket(Integer.MAX_VALUE, 10_0000);
		}
		for (int i = 0; i < 20; i++) {
			twenty.addBucket(10_0000, 10_0000);
		}
	}

	@TearDown
	public void tearDown() {
		context.close();
	}

	// 下面测量包含1个桶和40个桶时的执行时间

	@Benchmark
	public long buckets1() {
		return single.acquire(KEY, 100);
	}

	@Benchmark
	public long buckets40() {
		return twenty.acquire(KEY, 100);
	}

	public static void main(String[] args) throws Exception {
		var opt = new OptionsBuilder()
				.include(RedisTokenBucketPerf.class.getSimpleName())
				.build();
		var results = new Runner(opt).run();
	}
}
