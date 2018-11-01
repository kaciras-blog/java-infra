package net.kaciras.blog.infrastructure.typesession;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RequiredArgsConstructor
public class JacksonDefiniteSerializer implements DefiniteSerializer {

	private final ObjectMapper objectMapper;

	@Override
	public void serialize(Object value, OutputStream out) {
		try {
			objectMapper.writeValue(out, value);
		} catch (IOException e) {
			throw new RuntimeException("Json serialize failed.", e);
		}
	}

	@Override
	public <T> T deserialize(InputStream in, Class<T> type) {
		try {
			return objectMapper.readValue(in, type);
		} catch (IOException e) {
			throw new RuntimeException("Json deserialize failed.", e);
		}
	}
}
