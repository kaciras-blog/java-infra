package net.kaciras.blog.infrastructure.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.XReadArgs.StreamOffset;
import io.lettuce.core.api.StatefulRedisConnection;
import net.kaciras.blog.infrastructure.event.Event;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Redis5StreamTransmission implements AutoCloseable {

	static final XReadArgs READ_ARGS = XReadArgs.Builder.block(0);

	private static final String GROUP_KEY = "msg:unicast";
	private static final String BROADCAST_KEY = "msg:broadcast";

	private final RedisClient client;

	private final StatefulRedisConnection<String, String> sender;

	private final DirectProcessor<StreamMessage<String, String>> directProcessor = DirectProcessor.create();
	private final FieldMapSerializer serializer;

	private final GroupReceiver groupReceiver;
	private final Receiver receiver;

	public Redis5StreamTransmission(RedisProperties properties, FieldMapSerializer serializer, String group, String id) {
		this.serializer = serializer;

		client = RedisClient.create(RedisURI.builder()
				.withHost(properties.getHost())
				.withPort(properties.getPort())
				.withPassword(properties.getPassword())
				.build());

		sender = client.connect();
		groupReceiver = new GroupReceiver(GROUP_KEY, io.lettuce.core.Consumer.from(group, id));
		receiver = new Receiver(BROADCAST_KEY);

		receiver.start();
		groupReceiver.start();
	}

	/**
	 * 发送一个单播消息，该消息被Stream中每一个组中的任意一个消费者接收。
	 *
	 * @param event 消息
	 * @return 消息的ID
	 */
	public Mono<String> send(Event event) {
		return addToStream(GROUP_KEY, event);
	}

	public Mono<String> broadcast(Event event) {
		return addToStream(BROADCAST_KEY, event);
	}

	private Mono<String> addToStream(String streamName, Event event) {
		try {
			return sender.reactive().xadd(streamName, serializer.serialize(event));
		} catch (IllegalAccessException | JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public <T extends Event> Flux<T> revceive() {
		return directProcessor.map(this::deserialize);
	}

	private <T extends Event> T deserialize(StreamMessage<String, String> message) {
		try {
			var event = serializer.<T>deserialize(message.getBody());
			event.setEventId(message.getId());
			return event;
		} catch (ReflectiveOperationException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Override
	public void close() {
		receiver.connection.close();
		groupReceiver.connection.close();
		sender.close();
	}

	private final class Receiver implements Consumer<List<StreamMessage<String, String>>> {

		private final String streamName;
		private final StatefulRedisConnection<String, String> connection;

		private Receiver(String streamName) {
			this.streamName = streamName;
			connection = client.connect();
		}

		private void start() {
			connection.async().xread(READ_ARGS, StreamOffset.latest(streamName)).thenAccept(this);
		}

		@Override
		public void accept(List<StreamMessage<String, String>> messages) {
			var last = messages.get(messages.size() - 1);
			connection.async().xread(READ_ARGS, StreamOffset.from(streamName, last.getId())).thenAccept(this);
			messages.forEach(directProcessor::onNext);
		}
	}

	private final class GroupReceiver implements Consumer<List<StreamMessage<String, String>>> {

		private final String streamName;
		private final io.lettuce.core.Consumer<String> consumer;
		private final StatefulRedisConnection<String, String> connection;

		private GroupReceiver(String streamName, io.lettuce.core.Consumer<String> consumer) {
			this.streamName = streamName;
			this.consumer = consumer;
			connection = client.connect();
		}

		private void start() {
			connection.async().xreadgroup(consumer, READ_ARGS,
					XReadArgs.StreamOffset.latest(streamName)).thenAccept(this);
		}

		@Override
		public void accept(List<StreamMessage<String, String>> messages) {
			var last = messages.get(messages.size() - 1);
			connection.async().xreadgroup(consumer, READ_ARGS,
					XReadArgs.StreamOffset.from(streamName, last.getId())).thenAccept(this);
			messages.forEach(directProcessor::onNext);
		}
	}
}
