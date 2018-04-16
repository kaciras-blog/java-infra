package net.kaciras.blog.infrastructure.message;

import com.google.common.cache.CacheBuilder;
import io.reactivex.Single;
import io.reactivex.subjects.SingleSubject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class StandardMessageClient implements MessageClient {

	private final Transmission transmission;

	private final Map<Class, Collection<Consumer>> subs = new ConcurrentHashMap<>();
	private final Map<UUID, SingleSubject<ResultEvent>> callbacks;

	private Executor executor;

	public StandardMessageClient(Transmission transmission) {
		this(transmission, Executors.newSingleThreadExecutor());
	}

	public StandardMessageClient(Transmission transmission, Executor executor) {
		this.transmission = transmission;
		callbacks = CacheBuilder.newBuilder()
				.expireAfterWrite(10, TimeUnit.SECONDS)
				.<UUID, SingleSubject<ResultEvent>>build().asMap();
		waitForEvent();
		this.executor = executor;
	}

	private void waitForEvent() {
		transmission.getEventAsync().thenAcceptAsync(this::dispetch).thenAccept(kv -> waitForEvent());
	}

	@Override
	public <T extends DomainEvent> Single<ResultEvent> send(T event) {
		SingleSubject<ResultEvent> subject = SingleSubject.create();
		callbacks.put(event.getEventId(), subject);
		try {
			transmission.sendEvent(event);
		} catch (Exception e) {
			log.error("发送消息失败", e);
			callbacks.remove(event.getEventId()).onError(e);
		}
		return subject;
	}

	@Override
	public <T extends DomainEvent> void subscribe(Class<T> type, Consumer<T> consumer) {
		subs.computeIfAbsent(type, k -> new ArrayList<>()).add(consumer);
	}

	@SuppressWarnings("unchecked")
	private void dispetch(Event event) {
		if (event instanceof ResultEvent) {
			ResultEvent r = (ResultEvent) event;
			SingleSubject<ResultEvent> subject = callbacks.remove(r.getSourceId());
			if (subject == null) {
				return;
			}
			List<Exception> errors = r.getErrors();
			if(errors.isEmpty()) {
				subject.onSuccess(r);
			} else {
				subject.onError(errors.get(0));
			}
		} else {
			new DispetchWork((DomainEvent) event).dispetch();
		}
	}

	@RequiredArgsConstructor
	private final class DispetchWork {

		private final DomainEvent event;

		private final List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

		private int unfinished;
		private boolean allInvoked;

		void dispetch() {
			Class clazz = event.getClass();

			while (!clazz.equals(Event.class)) {
				Collection<Consumer> consumers = subs.get(clazz);
				clazz = clazz.getSuperclass();
				if (consumers == null) {
					continue;
				}
				consumers.forEach(this::invokeSubscriber);
			}
			finishInvoke();
		}

		private void invokeSubscriber(Consumer<DomainEvent> subscriber) {
			executor.execute(() -> {
				try {
					subscriber.accept(event);
				} catch (Exception ex) {
					errors.add(ex);
				} finally {
					notifyComplete();
				}
			});
			synchronized (this) {
				unfinished++;
			}
		}

		private synchronized void notifyComplete() {
			unfinished--;
			if(allInvoked && unfinished == 0) {
				sendResult();
			}
		}

		private synchronized void finishInvoke() {
			allInvoked = true;
			if(unfinished == 0) {
				sendResult();
			}
		}

		private void sendResult() {
			try {
				transmission.sendEvent(new ResultEvent(this.event.getEventId(), errors));
			} catch (Exception e) {
				log.error("发送消息失败", e);
			}
		}
	}
}
