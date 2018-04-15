package net.kaciras.blog.infrastructure.message;

import java.util.concurrent.*;

/**
 * @deprecated 使用TransferEventQueue代替
 */
@Deprecated
@SuppressWarnings("unchecked")
public class LinkedEventQueue implements Transmission {

	private final LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue<>();
	private final Executor executor;

	public LinkedEventQueue() {
		this(Executors.newSingleThreadExecutor());
	}

	public LinkedEventQueue(Executor executor) {
		this.executor = executor;
	}

	@Override
	public <T extends Event> void sendEvent(T event) {
		queue.add(event);
	}

	private <T extends Event> void doGetAndCallback(CompletableFuture<T> future) {
		try {
			future.complete((T) queue.take());
		} catch (Exception e) {
			Thread.interrupted();
			future.completeExceptionally(e);
		}
	}

	@Override
	public CompletionStage<Event> getEventAsync() {
		CompletableFuture<Event> future = new CompletableFuture<>();
		executor.execute(() -> doGetAndCallback(future));
		return future;
	}
}
