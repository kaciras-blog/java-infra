package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class InetAddressTypeHandlerTest {

	private static final TypeHandler<InetAddress> HANDLER = new InetAddressTypeHandler();

	private static final byte[] IP_BYTES_6789 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 66, 77, 88, 99};

	@Test
	void setParameter() throws Exception {
		var ps = mock(PreparedStatement.class);
		HANDLER.setParameter(ps, 1, InetAddress.getByName("66.77.88.99"), JdbcType.BINARY);
		Mockito.verify(ps).setBytes(1, IP_BYTES_6789);
	}

	@Test
	void getIpv4() throws Exception {
		var resultSet = mock(ResultSet.class);
		when(resultSet.getBytes(1)).thenReturn(IP_BYTES_6789);

		var addr = HANDLER.getResult(resultSet, 1);
		Assertions.assertEquals(InetAddress.getByName("66.77.88.99"), addr);
	}

	@Test
	void getIPv6() throws Exception {
		var youtube = InetAddress.getByName("2001:4860:4001:402::15");
		var resultSet = mock(ResultSet.class);
		when(resultSet.getBytes(1)).thenReturn(youtube.getAddress());

		var addr = HANDLER.getResult(resultSet, 1);
		Assertions.assertEquals(youtube, addr);
	}

	@Test
	void getResultByName() throws Exception {
		var youtube = InetAddress.getByName("2001:4860:4001:402::15");
		var resultSet = mock(ResultSet.class);
		when(resultSet.getBytes("ip")).thenReturn(youtube.getAddress());

		var addr = HANDLER.getResult(resultSet, "ip");
		Assertions.assertEquals(youtube, addr);
	}

	@Test
	void getResultFromCallable() throws Exception {
		var youtube = InetAddress.getByName("2001:4860:4001:402::15");
		var stat = mock(CallableStatement.class);
		when(stat.getBytes(5)).thenReturn(youtube.getAddress());

		var addr = HANDLER.getResult(stat, 5);
		Assertions.assertEquals(youtube, addr);
	}

	@Test
	void invalidData() throws Exception {
		var stat = mock(CallableStatement.class);
		when(stat.getBytes(5)).thenReturn(new byte[]{1, 2, 3, 4, 5, 6});

		Assertions.assertThrows(ResultMapException.class, () -> HANDLER.getResult(stat, 5));
	}
}
