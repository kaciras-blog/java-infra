package net.kaciras.blog.infrastructure.codec;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public final class ImageRefrenceSerializer extends JsonSerializer<ImageRefrence> {

	@Override
	public void serialize(ImageRefrence value, JsonGenerator gen,
						  SerializerProvider serializers) throws IOException {
		gen.writeString(value.toString());
	}
}
