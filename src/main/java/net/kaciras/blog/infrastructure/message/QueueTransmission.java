package net.kaciras.blog.infrastructure.message;

import java.util.concurrent.*;

public class QueueTransmission implements Transmission {

	private final Executor executor;
	private TransferQueue<Event> queue = new LinkedTransferQueue<>();

	public QueueTransmission() {
		this(Executors.newSingleThreadExecutor());
	}

	public QueueTransmission(Executor executor) {
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
