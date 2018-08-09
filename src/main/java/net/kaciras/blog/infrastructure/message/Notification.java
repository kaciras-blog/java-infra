package net.kaciras.blog.infrastructure.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kaciras.blog.infrastructure.event.Event;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class Notification {

	private final Event event;
	private final Map<Class<?>, Collection<Consumer>> subs;
	private final Executor executor;

	@Getter
	private final List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

	void invoke() {
		Class clazz = event.getClass();

		while (!clazz.equals(Object.class)) {
			var cs = subs.get(clazz);
			clazz = clazz.getSuperclass();

			if (cs == null) {
				continue;
			}
			cs.forEach(consumer -> executor.execute(() -> invoke(consumer)));
		}
	}

	private void invoke(Consumer<Event> subscriber) {
		try {
			subscriber.accept(event);
		} catch (Exception e) {
			errors.add(e);
			logger.debug("exception occured on handle message", e);
		}
	}

}
