package net.kaciras.blog.infrastructure.message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class JacksonJsonCodec implements Codec {

	private final ObjectMapper objectMapper;

	private final Map<String, Class> classMap = new HashMap<>();

	public JacksonJsonCodec(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		registerEvents(ResultEvent.class);
	}

	public JacksonJsonCodec() {
		this(new ObjectMapper().findAndRegisterModules()
				.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
				.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
	}

	/**
	 * 使用#deserialize方法直接读取对象之前，需要先注册事件对象的类型。
	 * 如果不使用#deserialize方法，此方法也无需调用。
	 *
	 * @param packageName 要扫描包名
	 * @return this
	 */
	public JacksonJsonCodec registerEvents(String packageName) {
		Reflections reflections = new Reflections(packageName);
		for (Class<? extends DomainEvent> aClass : reflections.getSubTypesOf(DomainEvent.class)) {
			classMap.put(aClass.getSimpleName(), aClass);
		}
		return this;
	}

	public JacksonJsonCodec registerEvents(Class<? extends Event>... clazz) {
		Arrays.stream(clazz).forEach(aClass -> classMap.put(aClass.getSimpleName(), aClass));
		return this;
	}

	public CommonMessageWrapper deserializeCommon(InputStream in) throws IOException {
		return objectMapper.readValue(in, CommonMessageWrapper.class);
	}

	public void serializeCommon(OutputStream out, CommonMessageWrapper object) throws IOException {
		objectMapper.writeValue(out, object);
	}

	public void serialize(OutputStream out, Event object) throws IOException {
		CommonMessageWrapper wrapper = new CommonMessageWrapper();
		Class clazz = object.getClass();

		wrapper.setType(clazz.getSimpleName());
		wrapper.setData(objectMapper.valueToTree(object));
		wrapper.setAncestors(new ArrayList<>(4));

		clazz = clazz.getSuperclass();
		while (!clazz.equals(Event.class)) {
			wrapper.getAncestors().add(clazz.getSimpleName());
			clazz = clazz.getSuperclass();
		}
		objectMapper.writeValue(out, wrapper);
	}

	public Event deserialize(InputStream in) throws IOException {
		CommonMessageWrapper wrapper = deserializeCommon(in);
		Class<Event> clazz = classMap.get(wrapper.getType()); //没检查继承
		if (clazz == null) {
			throw new IOException("未知的事件类型：" + wrapper.getType() + "，请使用registerEvents注册");
		}
		return objectMapper.treeToValue(wrapper.getData(), clazz);
	}

}
