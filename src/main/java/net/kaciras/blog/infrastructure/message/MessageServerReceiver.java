package net.kaciras.blog.infrastructure.message;

import java.util.concurrent.CompletionStage;

public class MessageServerReceiver implements EventReceiver {


	@Override
	public CompletionStage<Event> getEventAsync() {
		return null;
	}
}
