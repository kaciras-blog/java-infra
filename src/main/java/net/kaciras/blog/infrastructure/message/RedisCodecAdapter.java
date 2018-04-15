package net.kaciras.blog.infrastructure.message;

import io.lettuce.core.codec.RedisCodec;
import net.kaciras.blog.infrastructure.io.ByteBufferInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RedisCodecAdapter implements RedisCodec<String, Event> {

	private final Codec codec;

	public RedisCodecAdapter(Codec codec) {
		this.codec = codec;
	}

	@Override
	public String decodeKey(ByteBuffer buffer) {
		byte[] copy = new byte[buffer.remaining()];
		buffer.get(copy);
		return new String(copy, StandardCharsets.UTF_8);
	}

	@Override
	public Event decodeValue(ByteBuffer buffer) {
		try (InputStream ois = new ByteBufferInputStream(buffer)) {
			return codec.deserialize(ois);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public ByteBuffer encodeKey(String s) {
		return ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public ByteBuffer encodeValue(Event o) {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			codec.serialize(out, o);
			return ByteBuffer.wrap(out.toByteArray());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
