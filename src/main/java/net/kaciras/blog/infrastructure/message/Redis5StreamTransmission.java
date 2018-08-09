package net.kaciras.blog.infrastructure.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.XReadArgs.StreamOffset;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;
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

	private static final XReadArgs READ_ARGS = XReadArgs.Builder.block(0);

	private final StatefulRedisConnection<String, String> groupReceiver;
	private final StatefulRedisConnection<String, String> broadcastReceiver;

	private final StatefulRedisConnection<String, String> sender;

	private final DirectProcessor<StreamMessage<String, String>> directProcessor = DirectProcessor.create();
	private final FieldMapSerializer serializer;

	public Redis5StreamTransmission(RedisProperties properties, FieldMapSerializer serializer) {
		this.serializer = serializer;

		var client = RedisClient.create();
		var uri = RedisURI.builder()
				.withHost(properties.getHost())
				.withPort(properties.getPort())
				.withPassword(properties.getPassword())
				.build();

		groupReceiver = client.connect(uri);
		broadcastReceiver = client.connect(uri);
		sender = client.connect(uri);

		new Receiver("msg:group", groupReceiver).start();
		new Receiver("msg:broadcast", broadcastReceiver).start();
	}

	public Mono<String> send(Event event) {
		return addToStream("msg:group", event);
	}

	public Mono<String> broadcast(Event event) {
		return addToStream("msg:broadcast", event);
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
			T event = serializer.deserialize(message.getBody());
			//set id
			return event;
		} catch (ReflectiveOperationException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Override
	public void close() {
		groupReceiver.close();
		broadcastReceiver.close();
		sender.close();
	}

	@RequiredArgsConstructor
	private final class Receiver implements Consumer<List<StreamMessage<String, String>>> {

		private final String streamName;
		private final StatefulRedisConnection<String,String> connection;

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
}
