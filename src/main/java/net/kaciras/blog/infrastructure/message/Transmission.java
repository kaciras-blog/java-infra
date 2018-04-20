package net.kaciras.blog.infrastructure.message;

import java.util.concurrent.CompletionStage;

public interface Transmission extends AutoCloseable{

	CompletionStage<Event> getEventAsync();

	<T extends Event> void sendEvent(T event) throws Exception;
}
