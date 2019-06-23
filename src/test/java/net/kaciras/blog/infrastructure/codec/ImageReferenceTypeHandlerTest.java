package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
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

	@Override
	@Test
	void setParameter() throws Exception {
		HANDLER.setParameter(preparedStatement, 1, ImageReference.parse(NAME), JdbcType.BINARY);
		Mockito.verify(preparedStatement).setBytes(1, DATA);
	}

	@Override
	@Test
	void getResultFromResultSetByName() throws Exception {
		when(resultSet.getBytes("column")).thenReturn(DATA);
		assertThat(HANDLER.getResult(resultSet, "column"))
				.isEqualTo(ImageReference.parse(NAME));
	}

	@Override
	@Test
	void getResultFromResultSetByPosition() throws Exception {
		when(resultSet.getBytes(1)).thenReturn(DATA);
		assertThat(HANDLER.getResult(resultSet, 1))
				.isEqualTo(ImageReference.parse(NAME));
	}

	@Override
	@Test
	void getResultFromCallableStatement() throws Exception {
		when(callableStatement.getBytes(1)).thenReturn(DATA);
		assertThat(HANDLER.getResult(callableStatement, 1))
				.isEqualTo(ImageReference.parse(NAME));
	}
}
