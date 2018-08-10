package net.kaciras.blog.infrastructure.message;

import net.kaciras.blog.infrastructure.event.DomainEvent;

import java.util.function.Consumer;

public interface MessageClient extends AutoCloseable {

	<T extends DomainEvent> String send(T event);

	<T extends DomainEvent> String broadcast(T event);

	<T extends DomainEvent> void subscribe(Class<T> type, Consumer<T> consumer);
}
