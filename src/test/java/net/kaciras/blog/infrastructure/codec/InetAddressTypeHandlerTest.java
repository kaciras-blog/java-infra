package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

final class InetAddressTypeHandlerTest {

	private static final TypeHandler<InetAddress> HANDLER = new InetAddressTypeHandler();

	private static final byte[] IP_BYTES_6789 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 66, 77, 88, 99};

	@Test
	void setParameter() throws Exception {
		var ps = Mockito.mock(PreparedStatement.class);
		HANDLER.setParameter(ps, 1, InetAddress.getByName("66.77.88.99"), JdbcType.BINARY);
		Mockito.verify(ps).setBytes(1, IP_BYTES_6789);
	}

	@Test
	void getIpv4() throws Exception {
		var resultSet = Mockito.mock(ResultSet.class);
		Mockito.when(resultSet.getBytes(1)).thenReturn(IP_BYTES_6789);

		var addr = HANDLER.getResult(resultSet, 1);
		Assertions.assertEquals(InetAddress.getByName("66.77.88.99"), addr);
	}

	@Test
	void getIPv6() throws Exception {
		var youtube = InetAddress.getByName("2001:4860:4001:402::15");
		var resultSet = Mockito.mock(ResultSet.class);
		Mockito.when(resultSet.getBytes(1)).thenReturn(youtube.getAddress());

		var addr = HANDLER.getResult(resultSet, 1);
		Assertions.assertEquals(youtube, addr);
	}
}
