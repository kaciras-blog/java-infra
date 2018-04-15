package net.kaciras.blog.infrastructure.message;

import java.util.concurrent.CompletionStage;

public interface Transmission {

	CompletionStage<Event> getEventAsync();

	<T extends Event> void sendEvent(T event) throws Exception;
}
