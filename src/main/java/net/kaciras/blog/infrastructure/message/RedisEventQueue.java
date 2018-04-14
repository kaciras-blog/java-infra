package net.kaciras.blog.infrastructure.message;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class RedisEventQueue implements EventSender, EventReceiver, AutoCloseable {

	private final String queueName;

	private final RedisAsyncCommands<String, Event> popCommands;
	private final RedisAsyncCommands<String, Event> pushCommands;

	public RedisEventQueue(RedisClient client) {
		this(client, "EVENT.QUEUE");
	}

	public RedisEventQueue(RedisClient client, String queueName) {
		this.queueName = queueName;
		RedisJavaSerializeCodec<Event> codec = new RedisJavaSerializeCodec<>();
		popCommands = client.connect(codec).async();
		pushCommands = client.connect(codec).async();
	}

	@Override
	public void close() {
		popCommands.getStatefulConnection().close();
		pushCommands.getStatefulConnection().close();
	}

	@Override
	public CompletionStage<Event> getEventAsync() {
		return pushCommands.brpop(0, queueName).thenApply(k -> k.getValue());
	}

	@Override
	public <T extends Event> void sendEvent(T event) {
		popCommands.lpush(queueName, event);
	}
}
