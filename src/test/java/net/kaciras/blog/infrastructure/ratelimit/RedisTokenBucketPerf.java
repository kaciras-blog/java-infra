package net.kaciras.blog.infrastructure.ratelimit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Clock;
import java.util.Random;

@SuppressWarnings({"UnusedReturnValue", "unchecked"})
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Measurement(iterations = 5, time = 5)
public class RedisTokenBucketPerf {

	private ConfigurableApplicationContext context;

	private RedisTokenBucket single;
	private RedisTokenBucket twenty;

	private String randomKey;

	@Setup
	public void setUp() {
		context = new SpringApplicationBuilder()
				.sources(RedisTokenBucketTest.EmbeddedConfiguration.class)
				.web(WebApplicationType.NONE).run();
	}

	@Setup(Level.Iteration)
	public void setUpIteration() {
		var template = (RedisTemplate<String, Object>) context.getBean("testRedisTemplate");

		single = new RedisTokenBucket(Clock.systemDefaultZone(), template);
		single.addBucket(Integer.MAX_VALUE, 10_0000);

		twenty = new RedisTokenBucket(Clock.systemDefaultZone(), template);
		for (int i = 0; i < 10; i++) {
			twenty.addBucket(Integer.MAX_VALUE, 10_0000);
		}
		for (int i = 0; i < 10; i++) {
			twenty.addBucket(10_0000, 10_0000);
		}

		randomKey = "pref:" + Integer.toString(new Random().nextInt(), 36);
	}

	@TearDown
	public void tearDown() {
		context.close();
	}

	@Benchmark
	public long buckets1() {
		return single.acquire(randomKey, 100);
	}

	@Benchmark
	public long buckets20() {
		return twenty.acquire(randomKey, 100);
	}

	public static void main(String[] args) throws Exception {
		var opt = new OptionsBuilder()
				.include(RedisTokenBucketPerf.class.getSimpleName())
				.build();
		var results = new Runner(opt).run();
	}
}
