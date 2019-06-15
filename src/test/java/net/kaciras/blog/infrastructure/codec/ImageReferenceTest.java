package net.kaciras.blog.infrastructure.codec;

import net.kaciras.blog.infrastructure.exception.RequestArgumentException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

final class ImageReferenceTest {

	@Test
	void parseHash() {
		var name = "0FC3697B8E7787B53A76738016EB9355D812005CE6CFD354A3D6DBC812345678.png";
		var parse = ImageReference.parse(name);

		Assertions.assertThat(parse.getType()).isEqualTo(ImageType.PNG);
		Assertions.assertThat(parse.toString()).isEqualTo(name);
	}

	@Test
	void parseInternal() {
		var name = "picture.pcx";
		var parse = ImageReference.parse(name);

		Assertions.assertThat(parse.getType()).isEqualTo(ImageType.Internal);
		Assertions.assertThat(parse.toString()).isEqualTo(name);
	}

	@Test
	void parseInvalidName() {
		var invalidChar = "../any_system_file.sys";
		Assertions.assertThatThrownBy(() -> ImageReference.parse(invalidChar))
				.isInstanceOf(RequestArgumentException.class);
	}

	@Test
	void testEquality() {
		var imageA = new ImageReference("test.webp", ImageType.Internal);
		var imageB = ImageReference.parse("test.webp");

		Assertions.assertThat(imageA).isEqualTo(imageB);
		Assertions.assertThat(imageA.hashCode()).isEqualTo(imageB.hashCode());
	}
}
