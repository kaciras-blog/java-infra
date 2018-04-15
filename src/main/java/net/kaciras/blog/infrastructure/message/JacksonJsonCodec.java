package net.kaciras.blog.infrastructure.message;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class JacksonJsonCodec implements Codec{

	private final ObjectMapper objectMapper;

	private final Map<String, Class> classMap = new HashMap<>();

	public JacksonJsonCodec() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.findAndRegisterModules();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	public JacksonJsonCodec registerEvents(String packageName) {
		Reflections reflections = new Reflections(packageName);
		for (Class<? extends DomainEvent> aClass : reflections.getSubTypesOf(DomainEvent.class)) {
			classMap.put(aClass.getSimpleName(), aClass);
		}
		return this;
	}

	public void serialize(OutputStream out, Event object) throws IOException {
		ObjectNode tree = objectMapper.valueToTree(object);
		tree.put("class", object.getClass().getSimpleName());
		out.write(tree.toString().getBytes(StandardCharsets.UTF_8));
	}

	public Event deserialize(InputStream in) throws IOException {
		JsonNode tree = objectMapper.readTree(in);
		Class<Event> clazz = classMap.get(tree.get("class").asText());
		return objectMapper.treeToValue(tree, clazz);
	}
}
