package net.kaciras.blog.infrastructure.ratelimit;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public final class RedisBlockingLimiter implements RateLimiter {

	private final String namespace;
	private final RateLimiter inner;
	private final RedisConnectionFactory redisFactory;
	private final Clock clock;

	private List<Duration> blockTimes = Collections.emptyList();

	@Setter
	private boolean refreshOnReject;

	/**
	 * 设置封禁时间列表，列表中从前到后的等级逐渐升高，后面的时间必须大于前面的，所有时间都不能为负。
	 *
	 * @param blockTimes 封禁时间列表
	 * @throws IllegalArgumentException 如果参数不满足上述要求
	 */
	public void setBlockTimes(@NonNull List<Duration> blockTimes) {
		var max = Duration.ZERO;
		for (var time : blockTimes) {
			if (time.compareTo(max) < 0) {
				throw new IllegalArgumentException("封禁时间存在负数，或小于前面的");
			}
			max = time;
		}
		this.blockTimes = blockTimes;
	}

	/*
	 * 【更新】之前版本使用了异步化机制，将对内层限流器的调用和设置封禁记录这两操作放在其他线程中，
	 * 可以减少请求的执行时间。后来移除了，因为并非所有的限流算法都耗时较大，对于个别需要的算法可
	 * 以在其内部自己实现，或是做个异步装饰器类。
	 */
	@Override
	public long acquire(@NonNull String id, int permits) {

		// Simply delegate to inner when no blockTimes added.
		if (blockTimes.isEmpty()) {
			return inner.acquire(id, permits);
		}

		// Why RedisConnection not implements AutoClosable? Is it reminds user to call RedisConnectionUtils?
		var connection = redisFactory.getConnection();
		try {
			return doAcquire(connection, id, permits);
		} finally {
			RedisConnectionUtils.releaseConnection(connection, redisFactory);
		}
	}

	private long doAcquire(RedisConnection connection, String id, int permits) {
		var now = (int) clock.instant().getEpochSecond();
		var blockKey = (namespace + id).getBytes(StandardCharsets.UTF_8);
		var record = deserialize(connection.get(blockKey));

		if (record != null) {
			var bTime = record.getBlockingTime();
			var waitTime = record.beginTime + bTime - now;

			if (waitTime > 0) {
				if (refreshOnReject) {
					record.beginTime = now;
					connection.setEx(blockKey, bTime, record.serialize());
				}
				return waitTime;
			}
		}

		var waitTime = inner.acquire(id, permits);
		if (waitTime <= 0) {
			return waitTime;
		}
		if (record == null) {
			record = new BlockingRecord(0, now);
		} else {
			record.increaseLevel(now);
		}
		connection.setEx(blockKey, record.getObservationPeriod(), record.serialize());
		return record.getBlockingTime();
	}

	private BlockingRecord deserialize(@Nullable byte[] data) {
		if (data == null) {
			return null;
		}
		var buffer = ByteBuffer.wrap(data);
		return new BlockingRecord(buffer.getInt(), buffer.getInt());
	}

	// 仍然用的是32位秒数，最大2038年，本代码肯定用不到那么久
	@AllArgsConstructor
	private final class BlockingRecord {

		private int level;
		private int beginTime;

		private void increaseLevel(int now) {
			level = Math.min(level + 1, blockTimes.size() - 1);
			beginTime = now;
		}

		private long getBlockingTime() {
			return blockTimes.get(level).toSeconds();
		}

		private long getObservationPeriod() {
			var index = Math.min(level + 1, blockTimes.size() - 1);
			return blockTimes.get(index).toSeconds();
		}

		private byte[] serialize() {
			return ByteBuffer.allocate(8).putInt(level).putInt(beginTime).array();
		}
	}
}
