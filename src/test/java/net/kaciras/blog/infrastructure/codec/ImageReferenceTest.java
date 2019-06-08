package net.kaciras.blog.infrastructure.codec;

import net.kaciras.blog.infrastructure.exception.RequestArgumentException;
import org.apache.ibatis.type.JdbcType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

final class ImageReferenceTest {

	@Test
	void parseInternal() {
		var name = "picture.pcx";
		var parse = ImageReference.parse(name);

		Assertions.assertThat(parse.getType()).isEqualTo(ImageType.Internal);
		Assertions.assertThat(parse.toString()).isEqualTo(name);
	}

	@Test
	void parseHash() {
		var name = "0FC3697B8E7787B53A76738016EB9355D812005CE6CFD354A3D6DBC812345678.png";
		var parse = ImageReference.parse(name);

		Assertions.assertThat(parse.getType()).isEqualTo(ImageType.PNG);
		Assertions.assertThat(parse.toString()).isEqualTo(name);
	}

	@Test
	void parseInvalidName() {
		var invalidChar = "../any_system_file.sys";
		Assertions.assertThatThrownBy(() -> ImageReference.parse(invalidChar))
				.isInstanceOf(RequestArgumentException.class);
	}

	@Test
	void testBytesEncoding() throws Exception {
		var handler = new ImageReferenceTypeHandler();
		var name = "picture.pcx";
		var imageReference = ImageReference.parse(name);

		var statement = Mockito.mock(PreparedStatement.class);
		handler.setNonNullParameter(statement, 1, imageReference, JdbcType.BINARY);

		var bytes = new byte[ImageReference.HASH_SIZE + 1];
		System.arraycopy(name.getBytes(), 0, bytes, 2, name.length());
		bytes[1] = (byte) name.length();
		Mockito.verify(statement).setBytes(1, bytes);
	}

	@Test
	void testBytesDecoding() throws Exception {
		var name = "picture.pcx";
		var bytes = new byte[ImageReference.HASH_SIZE + 1];
		System.arraycopy(name.getBytes(), 0, bytes, 2, name.length());
		bytes[1] = (byte) name.length();
		var handler = new ImageReferenceTypeHandler();

		var resultSet = Mockito.mock(ResultSet.class);
		Mockito.when(resultSet.getBytes(1)).thenReturn(bytes);
		var imageReference = handler.getNullableResult(resultSet, 1);

		Assertions.assertThat(imageReference.getName()).isEqualTo(name);
		Assertions.assertThat(imageReference.getType()).isEqualTo(ImageType.Internal);
	}
}
