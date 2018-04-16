package net.kaciras.blog.infrastructure.codec;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.regex.Pattern;

public final class ImageRefrenceDeserializer extends JsonDeserializer<ImageRefrence> {

	@Override
	public ImageRefrence deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return ImageRefrence.parse(p.getText());
	}
}
