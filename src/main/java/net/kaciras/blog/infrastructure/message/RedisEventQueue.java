package net.kaciras.blog.infrastructure.message;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.concurrent.CompletionStage;

public class RedisEventQueue implements Transmission {

	private final String queueName;

	private final RedisAsyncCommands<String, Event> popCommands;
	private final RedisAsyncCommands<String, Event> pushCommands;

	public RedisEventQueue(RedisClient client) {
		this(client, "EVENT.QUEUE");
	}

	public RedisEventQueue(RedisClient client, String queueName) {
		this.queueName = queueName;
		JacksonJsonCodec codec = new JacksonJsonCodec()
				.registerEvents("net.kaciras.blog.infrastructure.event");
		RedisCodecAdapter adapter = new RedisCodecAdapter(codec);

		popCommands = client.connect(adapter).async();
		pushCommands = client.connect(adapter).async();
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
