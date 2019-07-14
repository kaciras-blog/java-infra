package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

	@Test
	void hexName() throws SQLException {
		var name = "4c21ffdf38ae94a4108ed27d3c650d55fce4798438795d42be4991d0333c0208";
		var bytes = ByteBuffer.allocate(33).put((byte)2).put(CodecUtils.decodeHex(name)).array();
		when(resultSet.getBytes(1)).thenReturn(bytes);

		assertThat(HANDLER.getResult(resultSet, 1))
				.isEqualTo(new ImageReference(name, ImageType.WEBP));
	}

	@Test
	void invalidValue() throws SQLException {
		when(resultSet.getBytes(1)).thenReturn("invalid".getBytes());

		assertThatThrownBy(() -> HANDLER.getResult(resultSet, 1))
				.isInstanceOf(ResultMapException.class)
				.hasCauseInstanceOf(SQLException.class);
	}
}
