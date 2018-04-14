package net.kaciras.blog.infrastructure.message;

import com.google.common.cache.CacheBuilder;
import io.reactivex.Single;
import io.reactivex.subjects.SingleSubject;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class StandardMessageClient implements MessageClient {

	private final EventSender sender;
	private final EventReceiver receiver;

	private final Map<Class, Collection<Consumer>> subs = new ConcurrentHashMap<>();
	private final Map<UUID, SingleSubject<ResultEvent>> callbacks;

	@Setter
	private Executor executor;

	public StandardMessageClient(EventSender sender, EventReceiver receiver) {
		this(sender, receiver, Executors.newSingleThreadExecutor());
	}

	public StandardMessageClient(EventSender sender, EventReceiver receiver, Executor executor) {
		this.sender = sender;
		this.receiver = receiver;
		callbacks = CacheBuilder.newBuilder()
				.expireAfterWrite(10, TimeUnit.SECONDS)
				.<UUID, SingleSubject<ResultEvent>>build()
				.asMap();
		waitForEvent();
		this.executor = executor;
	}

	private void waitForEvent() {
		receiver.getEventAsync().thenAcceptAsync(this::dispetch).thenAccept(kv -> waitForEvent());
	}

	@Override
	public <T extends DomainEvent> Single<ResultEvent> send(T event) {
		SingleSubject<ResultEvent> subject = SingleSubject.create();
		callbacks.put(event.getEventId(), subject);
		sender.sendEvent(event);
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

	private final class DispetchWork {

		private final DomainEvent event;

		private final List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

		private int unfinished;
		private boolean allInvoked;

		private DispetchWork(DomainEvent event) {
			this.event = event;
		}

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
			sender.sendEvent(new ResultEvent(this.event.getEventId(), errors));
		}
	}
}
