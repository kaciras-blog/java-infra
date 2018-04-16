package net.kaciras.blog.infrastructure.codec;

import net.kaciras.blog.infrastructure.exception.RequestArgumentException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImageRefrenceTest {

	@Test
	void testParse() {
		String fn = "picture.pcx";
		ImageRefrence parse = ImageRefrence.parse(fn);
		Assertions.assertThat(parse.toString()).isEqualTo(fn);

		fn = "0FC3697B8E7787B53A76738016EB9355D812005CE6CFD354A3D6DBC812345678.png";
		parse = ImageRefrence.parse(fn);
		Assertions.assertThat(parse.getType()).isEqualTo(ImageType.PNG);
		Assertions.assertThat(parse.toString()).isEqualTo(fn);

		String dangerous = "../any_system_file.sys";
		Assertions.assertThatThrownBy(() -> ImageRefrence.parse(dangerous)).isInstanceOf(RequestArgumentException.class);
	}
}
