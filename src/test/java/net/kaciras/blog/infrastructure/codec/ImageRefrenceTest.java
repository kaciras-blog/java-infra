package net.kaciras.blog.infrastructure.codec;

import net.kaciras.blog.infrastructure.exception.RequestArgumentException;
import org.apache.ibatis.type.JdbcType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ImageRefrenceTest {

	@Test
	void testParseInternal() {
		var name = "picture.pcx";
		var parse = ImageRefrence.parse(name);

		Assertions.assertThat(parse.getType()).isEqualTo(ImageType.Internal);
		Assertions.assertThat(parse.toString()).isEqualTo(name);
	}

	@Test
	void testParseHash() {
		var name = "0FC3697B8E7787B53A76738016EB9355D812005CE6CFD354A3D6DBC812345678.png";
		var parse = ImageRefrence.parse(name);

		Assertions.assertThat(parse.getType()).isEqualTo(ImageType.PNG);
		Assertions.assertThat(parse.toString()).isEqualTo(name);
	}

	@Test
	void parseInvaildName() {
		var invaildChar = "../any_system_file.sys";
		Assertions.assertThatThrownBy(() -> ImageRefrence.parse(invaildChar))
				.isInstanceOf(RequestArgumentException.class);
	}

	@Test
	void testBytesEncoding() throws Exception {
		var handler = new ImageRefrenceTypeHandler();
		var name = "picture.pcx";
		var imageRefrence = ImageRefrence.parse(name);

		var statment = Mockito.mock(PreparedStatement.class);
		handler.setNonNullParameter(statment, 1, imageRefrence, JdbcType.BINARY);

		var bytes = new byte[ImageRefrence.HASH_SIZE + 1];
		System.arraycopy(name.getBytes(), 0, bytes, 2, name.length());
		bytes[1] = (byte) name.length();
		Mockito.verify(statment).setBytes(1, bytes);
	}

	@Test
	void testBytesDecoding() throws Exception {
		var name = "picture.pcx";
		var bytes = new byte[ImageRefrence.HASH_SIZE + 1];
		System.arraycopy(name.getBytes(), 0, bytes, 2, name.length());
		bytes[1] = (byte) name.length();
		var handler = new ImageRefrenceTypeHandler();

		var resultSet = Mockito.mock(ResultSet.class);
		Mockito.when(resultSet.getBytes(1)).thenReturn(bytes);
		var imageRefrence = handler.getNullableResult(resultSet, 1);

		Assertions.assertThat(imageRefrence.getName()).isEqualTo(name);
		Assertions.assertThat(imageRefrence.getType()).isEqualTo(ImageType.Internal);
	}
}
