package net.kaciras.blog.infrastructure.message;

import lombok.extern.slf4j.Slf4j;
import net.kaciras.blog.infrastructure.event.DomainEvent;
import net.kaciras.blog.infrastructure.event.Event;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
public class StandardMessageClient implements MessageClient {

	private final Redis5StreamTransmission transmission;

	private final Map<Class<?>, DirectProcessor> subs = new ConcurrentHashMap<>();

	private Executor executor;

	public StandardMessageClient(Redis5StreamTransmission transmission) {
		this(transmission, Runnable::run);
	}

	public StandardMessageClient(Redis5StreamTransmission transmission, Executor executor) {
		this.transmission = transmission;
		this.executor = executor;
		transmission.revceive().subscribe(this::dispatch);
	}

	@Override
	public <T extends DomainEvent> String send(T event) {
		return transmission.send(event).block();
	}

	@Override
	public <T extends DomainEvent> String broadcast(T event) {
		return transmission.broadcast(event).block();
	}

	@SuppressWarnings("unchecked")
	private <T extends Event> void dispatch(T event) {
		Class<?> clazz = event.getClass();
		while (!clazz.equals(Event.class)) {
			var consumers = subs.get(clazz);
			clazz = clazz.getSuperclass();
			if (consumers != null) {
				consumers.onNext(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DomainEvent> Flux<T> subscribe(Class<T> type) {
		return subs.computeIfAbsent(type, k -> DirectProcessor.create());
	}

	@Override
	public void close() {
		transmission.close();
	}
}
