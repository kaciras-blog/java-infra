package net.kaciras.blog.infrastructure.message;

import net.kaciras.blog.infrastructure.event.DomainEvent;
import net.kaciras.blog.infrastructure.event.Event;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public abstract class AbstractDispatcher implements MessageClient {

	private final Map<Class, FluxProcessor> subs = new ConcurrentHashMap<>();
	private final FluxProcessor NOP = DirectProcessor.create();

	@Override
	public <T extends DomainEvent> Flux<T> getChannel(Class<T> type) {
		return subs.computeIfAbsent(type, k -> DirectProcessor.create());
	}

	protected <T extends Event> void dispatch(T event) {
		for (Class<?> clazz = event.getClass();
			 !clazz.equals(Event.class);
			 clazz = clazz.getSuperclass()) {
			subs.getOrDefault(clazz, NOP).onNext(event);
		}
	}
}
