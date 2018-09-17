package net.kaciras.blog.infrastructure.codec;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 将ImageRefrence和Json(com.fasterxml.jackson)互相转换的工具。
 */
public final class ImageRefrenceJson {

	private static final String DIRECTORY = "/image/"; // 图片直接存储在前端服务器上

	static final class Serializer extends JsonSerializer<ImageRefrence> {

		@Override
		public void serialize(ImageRefrence value,
							  JsonGenerator gen,
							  SerializerProvider serializers) throws IOException {
			gen.writeString(DIRECTORY + value.toString());
		}
	}

	static final class Deserializer extends JsonDeserializer<ImageRefrence> {

		@Override
		public ImageRefrence deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
			return ImageRefrence.parse(p.getText().substring(DIRECTORY.length()));
		}
	}
}
