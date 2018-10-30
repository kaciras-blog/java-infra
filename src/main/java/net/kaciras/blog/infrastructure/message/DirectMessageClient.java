package net.kaciras.blog.infrastructure.message;

import net.kaciras.blog.infrastructure.event.DomainEvent;
import reactor.core.publisher.DirectProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是一个‘假的’消息队列，它将直接在发送线程调用订阅者的方法。
 * 这个实现只能在进程内使用，并且消息的通知是同步的。
 * <p>
 * 使用此实现避免分布式消息的事务问题，以便于直接使用基于线程的事务管理机制（例
 * 如Spring的@Transactional），同时也能够向发送者屏蔽订阅者，实现解耦。
 */
public final class DirectMessageClient extends AbstractDispatcher {

	private final Map<Class, DirectProcessor> subs = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public <T extends DomainEvent> String send(T event) {
		dispatch(event);
		return event.getEventId();
	}

	@Override
	public <T extends DomainEvent> String broadcast(T event) {
		return send(event);
	}

	@Override
	public void close() {}
}
