package net.kaciras.blog.infrastructure.codec;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.regex.Pattern;

public final class ImageRefrenceDeserializer extends JsonDeserializer<ImageRefrence> {

	private final Pattern regex = Pattern.compile("$[0-9A-F]{" + (ImageRefrence.HASH_SIZE << 1) + "}\\.");

	@Override
	public ImageRefrence deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String text = p.getText();
		String[] split = text.split("\\.");
		ImageRefrence refrence = new ImageRefrence();

		if(regex.matcher(text).find()) {
			refrence.setName(split[0]);
			refrence.setType(ImageType.valueOf(split[1]));
		} else {
			refrence.setName(text);
			refrence.setType(ImageType.Internal);
		}
		return refrence;
	}
}
