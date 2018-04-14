package net.kaciras.blog.infrastructure.message;

public interface EventSender {

	<T extends Event> void sendEvent(T event);
}
