package net.kaciras.blog.infrastructure.message;

import net.kaciras.blog.infrastructure.event.DomainEvent;
import reactor.core.publisher.Flux;

public interface MessageClient extends AutoCloseable {

	<T extends DomainEvent> String send(T event);

	<T extends DomainEvent> String broadcast(T event);

	<T extends DomainEvent> Flux<T> subscribe(Class<T> type);
}
