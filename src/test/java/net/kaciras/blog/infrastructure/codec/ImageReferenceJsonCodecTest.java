package net.kaciras.blog.infrastructure.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ImageReferenceJsonCodecTest {

	private ObjectReader reader;
	private ObjectWriter writer;

	@BeforeEach
	void setUp() {
		var objectMapper = new ObjectMapper();
		objectMapper.registerModule(new ExtendsCodecModule());
		reader = objectMapper.readerFor(ImageReference.class);
		writer = objectMapper.writerFor(ImageReference.class);
	}

	@Test
	void serializeStaticResource() throws Exception {
		var image = ImageReference.parse("测试图片.webp");
		var json = writer.writeValueAsString(image);
		assertThat(json).isEqualTo("\"/static/img/测试图片.webp\"");
	}

	@Test
	void serializeImageServer() throws Exception {
		var image = ImageReference.parse("0FC3697B8E7787B53A76738016EB9355D812005CE6CFD354A3D6DBC812345678.png");
		var json = writer.writeValueAsString(image);
		assertThat(json).isEqualTo("\"/image/0FC3697B8E7787B53A76738016EB9355D812005CE6CFD354A3D6DBC812345678.png\"");
	}

	@Test
	void deserializeStaticResource() throws Exception {
		var name = "测试图片.webp";
		var json = "\"/static/img/" + name + "\"";
		ImageReference image = reader.readValue(json);

		assertThat(image.getType()).isEqualTo(ImageType.Internal);
		assertThat(image.toString()).isEqualTo(name);
	}

	@Test
	void deserializeImageServer() throws Exception {
		var name = "0FC3697B8E7787B53A76738016EB9355D812005CE6CFD354A3D6DBC812345678.png";
		var json = "\"/image/" + name + "\"";
		ImageReference image = reader.readValue(json);

		assertThat(image.getType()).isEqualTo(ImageType.PNG);
		assertThat(image.toString()).isEqualTo(name);
	}

	@Test
	void deserializeInvalid() {
		var json = "\"https://www.example.com/image/666.jpg\"";
		Assertions.assertThatThrownBy(() -> reader.readValue(json)).isInstanceOf(JsonProcessingException.class);
	}
}
