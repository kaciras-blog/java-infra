package net.kaciras.blog.infrastructure.io;

import java.io.InputStream;
import java.nio.ByteBuffer;

public final class ByteBufferInputStream extends InputStream {

	private final ByteBuffer buffer;

	public ByteBufferInputStream(ByteBuffer b) {
		this.buffer = b;
	}

	@Override
	public int available() {
		return buffer.remaining();
	}

	@Override
	public int read() {
		if (buffer.remaining() > 0) {
			return (buffer.get() & 0xFF);
		}
		return -1;
	}
}
