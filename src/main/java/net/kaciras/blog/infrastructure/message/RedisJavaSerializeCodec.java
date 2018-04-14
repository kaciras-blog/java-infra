package net.kaciras.blog.infrastructure.message;

import io.lettuce.core.codec.RedisCodec;
import net.kaciras.blog.infrastructure.io.ByteBufferInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RedisJavaSerializeCodec<T> implements RedisCodec<String, T> {

	@Override
	public String decodeKey(ByteBuffer buffer) {
		byte[] copy = new byte[buffer.remaining()];
		buffer.get(copy);
		return new String(copy, StandardCharsets.UTF_8);
	}

	@Override
	public T decodeValue(ByteBuffer buffer) {
		try (ObjectInputStream ois = new ObjectInputStream(new ByteBufferInputStream(buffer))) {
			return (T) ois.readObject();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (ClassNotFoundException e) {
			throw new Error("未知的Class类型", e);
		}
	}

	@Override
	public ByteBuffer encodeKey(String s) {
		return ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public ByteBuffer encodeValue(T o) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(ObjectOutput out = new ObjectOutputStream(baos)) {
			out.writeObject(o);
			return ByteBuffer.wrap(baos.toByteArray());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
