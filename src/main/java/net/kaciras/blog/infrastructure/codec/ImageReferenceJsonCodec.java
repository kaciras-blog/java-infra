package net.kaciras.blog.infrastructure.codec;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 将 ImageReference 和Json(com.fasterxml.jackson)互相转换的工具。
 * 转换时会加上图片所在服务器的URL前缀。
 */
final class ImageReferenceJsonCodec {

	private static final String DIRECTORY = "/image/"; // 图片直接存储在前端服务器上

	static final class Serializer extends JsonSerializer<ImageReference> {

		@Override
		public void serialize(ImageReference value,
							  JsonGenerator gen,
							  SerializerProvider serializers) throws IOException {
			gen.writeString(DIRECTORY + value.toString());
		}
	}

	static final class Deserializer extends JsonDeserializer<ImageReference> {

		@Override
		public ImageReference deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
			return ImageReference.parse(p.getText().substring(DIRECTORY.length()));
		}
	}
}
