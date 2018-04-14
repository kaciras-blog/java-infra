package net.kaciras.blog.infrastructure.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

@RequiredArgsConstructor
public class JacksonJsonCodec {

	private final ObjectMapper objectMapper;

	public JacksonJsonCodec() {
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	public void serialize(OutputStream out, Object object) throws IOException {
		objectMapper.writeValue(out, object);
	}

	public Object deserialize(InputStream in) throws IOException {
		return objectMapper.readTree(in);
	}
}
