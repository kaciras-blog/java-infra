package net.kaciras.blog.infrastructure.message;

import lombok.extern.slf4j.Slf4j;
import net.kaciras.blog.infrastructure.event.DomainEvent;

import java.util.concurrent.Executor;

@Slf4j
public class StandardMessageClient extends AbstractDispatcher {

	private final Redis5StreamTransmission transmission;

	private Executor executor;

	public StandardMessageClient(Redis5StreamTransmission transmission) {
		this(transmission, Runnable::run);
	}

	public StandardMessageClient(Redis5StreamTransmission transmission, Executor executor) {
		this.transmission = transmission;
		this.executor = executor;
		transmission.revceive().subscribe(super::dispatch);
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
	public void close() {
		transmission.close();
	}
}
