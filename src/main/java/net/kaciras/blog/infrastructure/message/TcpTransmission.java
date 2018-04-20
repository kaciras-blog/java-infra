package net.kaciras.blog.infrastructure.message;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public class TcpTransmission implements Transmission {

	private final Socket socket;
	private final Codec codec;
	private final Executor executor;

	public TcpTransmission(String host, int port, Codec codec, Executor executor) throws IOException {
		socket = new Socket(host, port);
		this.codec = codec;
		this.executor = executor;
	}

	public void authenticate(String password) throws IOException {
		socket.getOutputStream().write(password.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public CompletionStage<Event> getEventAsync() {
		CompletableFuture<Event> future = new CompletableFuture<>();
		executor.execute(() -> readEvent(future));
		return future;
	}

	private void readEvent(CompletableFuture<Event> future) {
		try {
			future.complete(codec.deserialize(socket.getInputStream()));
		} catch (IOException e) {
			future.completeExceptionally(e);
		}
	}

	@Override
	public <T extends Event> void sendEvent(T event) throws IOException {
		codec.serialize(socket.getOutputStream(), event);
	}

	@Override
	public void close() throws Exception {
		socket.close();
	}
}
