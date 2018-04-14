package net.kaciras.blog.infrastructure.message;

import java.util.concurrent.*;

public class TransferEventQueue implements EventSender, EventReceiver {

	private final Executor executor;
	private TransferQueue<Event> queue = new LinkedTransferQueue<>();

	public TransferEventQueue() {
		this(Executors.newSingleThreadExecutor());
	}

	public TransferEventQueue(Executor executor) {
		this.executor = executor;
	}

	private void doGetAndCallback(CompletableFuture<Event> future) {
		try {
			future.complete(queue.take());
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

	@Override
	public <T extends Event> void sendEvent(T event) {
		queue.add(event);
	}
}
