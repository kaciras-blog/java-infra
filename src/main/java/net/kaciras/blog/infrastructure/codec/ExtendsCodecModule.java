package net.kaciras.blog.infrastructure.codec;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

/**
 * A jackson module include serializers/deserializers in package.
 */
public final class ExtendsCodecModule extends Module {

	@Override
	public String getModuleName() {
		return "KacirasBlogInfrastructureCodec";
	}

	@Override
	public Version version() {
		return new Version(1, 0, 0, "", null, null);
	}

	@Override
	public void setupModule(SetupContext context) {
		var serializers = new SimpleSerializers();
		serializers.addSerializer(ImageReference.class, new ImageReferenceJsonCodec.Serializer());
		context.addSerializers(serializers);

		var deserializers = new SimpleDeserializers();
		deserializers.addDeserializer(ImageReference.class, new ImageReferenceJsonCodec.Deserializer());
		context.addDeserializers(deserializers);
	}
}
