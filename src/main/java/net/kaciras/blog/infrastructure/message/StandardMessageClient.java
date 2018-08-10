package net.kaciras.blog.infrastructure.message;

import lombok.extern.slf4j.Slf4j;
import net.kaciras.blog.infrastructure.event.DomainEvent;
import net.kaciras.blog.infrastructure.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
public class StandardMessageClient implements MessageClient {

	private final Redis5StreamTransmission transmission;

	private final Map<Class<?>, Collection<Consumer>> subs = new ConcurrentHashMap<>();

	private Executor executor;

	public StandardMessageClient(Redis5StreamTransmission transmission) {
		this(transmission, Runnable::run);
	}

	public StandardMessageClient(Redis5StreamTransmission transmission, Executor executor) {
		this.transmission = transmission;
		this.executor = executor;
		transmission.revceive().subscribe(this::dispetch);
	}

	@Override
	public <T extends DomainEvent> String send(T event) {
		return transmission.send(event).block();
	}

	@Override
	public <T extends DomainEvent> String broadcast(T event) {
		return transmission.broadcast(event).block();
	}

	@Override
	public <T extends DomainEvent> void subscribe(Class<T> type, Consumer<T> consumer) {
		subs.computeIfAbsent(type, k -> new ArrayList<>()).add(consumer);
	}

	@SuppressWarnings("unchecked")
	private void dispetch(Event event) {
		new Notification(event, subs, executor).invoke();
	}

	@Override
	public void close() {
		transmission.close();
	}

}
