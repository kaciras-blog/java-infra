package net.kaciras.blog.infrastructure.ratelimit;

/**
 * 限流器，用于限制对某个资源的访问速率。
 *
 * 每一次访问都需要获取一定数量的令牌，令牌用来衡量一次访问对资源的消耗程度，越大表示越耗资源，需要
 * 被限制在更低的速率。
 */
public interface RateLimiter {

	/**
	 * 获取指定数量的令牌，返回桶内拥有足够令牌所需要等待的时间。
	 *
	 * @param id      标识获取者的身份，一般是对方的IP之类的
	 * @param permits 要获取的令牌数量
	 * @return 需要等待的时间（秒），0表示成功，小于0表示永远无法完成
	 */
	long acquire(String id, int permits);
}
