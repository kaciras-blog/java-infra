package net.kaciras.blog.infrastructure.ratelimit;

/**
 * 限流器，用于限制对某个资源的访问速率。
 *
 * 每一次访问都需要获取一定数量的令牌，令牌用来衡量一次访问对资源的消耗程度，越大表示越耗资源，需要
 * 被限制在更低的速率。
 */
public interface RateLimiter {

	/**
	 * 获取指定数量的令牌，如果无法获取则返回一个时间，表示请求方需要至少等待该时间之后
	 * 才有可能获取成功；如果获取成功则返回0。
	 *
	 * 如果返回了一个负数，表明该请求无论如何都无法通过。
	 *
	 * @param id      标识获取者的身份，一般是对方的IP之类的
	 * @param permits 要获取的令牌数量
	 * @return 需要等待的时间（秒），0表示成功，小于0表示永远无法完成
	 */
	long acquire(String id, int permits);
}
