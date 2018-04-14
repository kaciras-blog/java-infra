package net.kaciras.blog.infrastructure.message;

import java.util.concurrent.CompletionStage;

public interface EventReceiver {

	CompletionStage<Event> getEventAsync();
}
