package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

final class ImageReferenceTypeHandlerTest extends AbstractTypeHandlerTest {

	private static final TypeHandler<ImageReference> HANDLER = new ImageReferenceTypeHandler();

	private static final String NAME = "picture.png";
	private static final byte[] DATA;

	static {
		DATA = new byte[ImageReference.HASH_SIZE + 1];
		System.arraycopy(NAME.getBytes(), 0, DATA, 2, NAME.length());
		DATA[1] = (byte) NAME.length();
	}

	@Test
	void setParameter() throws Exception {
		HANDLER.setParameter(preparedStatement, 1, ImageReference.parse(NAME), JdbcType.BINARY);
		Mockito.verify(preparedStatement).setBytes(1, DATA);
	}

	// (ResultSet|CallableStatement) 的 getXXX 没法抽象，只能一个个写
	@Test
	void getResultFromResultSetByName() throws Exception {
		when(resultSet.getBytes("column")).thenReturn(DATA);
		Assertions.assertThat(HANDLER.getResult(resultSet, "column"))
				.isEqualTo(ImageReference.parse(NAME));
	}

	@Test
	void getResultFromResultSetByPosition() throws Exception {
		when(resultSet.getBytes(1)).thenReturn(DATA);
		Assertions.assertThat(HANDLER.getResult(resultSet, 1))
				.isEqualTo(ImageReference.parse(NAME));
	}

	@Test
	void getResultFromCallableStatement() throws Exception {
		when(callableStatement.getBytes(1)).thenReturn(DATA);
		Assertions.assertThat(HANDLER.getResult(callableStatement, 1))
				.isEqualTo(ImageReference.parse(NAME));
	}
}
