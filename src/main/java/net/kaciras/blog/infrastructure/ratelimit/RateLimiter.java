package net.kaciras.blog.infrastructure.ratelimit;

public interface RateLimiter {

	long acquire(String id, int permits);
}
