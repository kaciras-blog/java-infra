package net.kaciras.blog.infrastructure.message;

import net.kaciras.blog.infrastructure.event.DomainEvent;
import reactor.core.publisher.Flux;

public interface MessageClient extends AutoCloseable {

	<T extends DomainEvent> String send(T event);

	<T extends DomainEvent> String broadcast(T event);

	/**
	 * 订阅某种类型的消息。
	 * 虽然 Flux.ofType 也能够过滤类型，但考虑到优化，还是在参数中就带上类型。
	 *
	 * @param type 消息类
	 * @param <T> 消息类型
	 * @return 发布符合类型消息的Flux
	 */
	<T extends DomainEvent> Flux<T> subscribe(Class<T> type);
}
