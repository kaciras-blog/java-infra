package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

final class ImageReferenceTypeHandlerTest {

	private static final TypeHandler<ImageReference> HANDLER = new ImageReferenceTypeHandler();

	@Test
	void testBytesEncoding() throws Exception {
		var name = "picture.pcx";
		var imageReference = ImageReference.parse(name);

		var statement = Mockito.mock(PreparedStatement.class);
		HANDLER.setParameter(statement, 1, imageReference, JdbcType.BINARY);

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

		var resultSet = Mockito.mock(ResultSet.class);
		Mockito.when(resultSet.getBytes(1)).thenReturn(bytes);
		var imageReference = HANDLER.getResult(resultSet, 1);

		Assertions.assertThat(imageReference.getName()).isEqualTo(name);
		Assertions.assertThat(imageReference.getType()).isEqualTo(ImageType.Internal);
	}
}
