package net.kaciras.blog.infrastructure.message;

public class MessageServer {

	private final EventReceiver receiver;

	public MessageServer(EventReceiver receiver) {
		this.receiver = receiver;
	}


}
