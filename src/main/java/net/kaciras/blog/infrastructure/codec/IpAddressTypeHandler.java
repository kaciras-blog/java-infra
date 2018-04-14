package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;

/**
 * 将InetAddress转换成长度为16的字节数组存储在数据库中。
 * IPv4将使用 IPv4 Mapped IPv6 Address格式存储。
 */
public final class IpAddressTypeHandler extends BaseTypeHandler<InetAddress> {

	private static final byte[] prefix = new byte[12];

	static { prefix[10] = prefix[11] = (byte)0xFF; }

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, InetAddress address, JdbcType jdbcType) throws SQLException {
		ps.setBytes(i, encode(address));
	}

	@Override
	public InetAddress getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return decode(rs.getBytes(columnName));
	}

	@Override
	public InetAddress getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return decode(rs.getBytes(columnIndex));
	}

	@Override
	public InetAddress getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return decode(cs.getBytes(columnIndex));
	}

	/**
	 * 把字节数组转换为InetAddress
	 *
	 * @param bytes 字节数组
	 * @return InetAddress
	 * @throws SQLDataException 读取的数据不是IP地址
	 */
	private InetAddress decode(byte[] bytes) throws SQLDataException {
		try {
			//InetAddress.getByAddress()能自动识别IPv4-mapped addresses
			return InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			throw new SQLDataException("读取的数据不是IP地址", e);
		}
	}

	/**
	 * 把InetAddress转换为16字节的数组
	 *
	 * @param address 地址
	 * @return 字节数组
	 */
	private byte[] encode(InetAddress address) {
		if(address instanceof Inet6Address) {
			return address.getAddress();
		}
		return mappingToIPv6(address.getAddress());
	}

	/**
	 * 使用IPv4-mapped addresses,将IPv4的4字节地址转换成IPv6的16字节地址
	 *
	 * @see <a href="https://tools.ietf.org/html/rfc3493#section-3.7">IPv4-mapped addresses</a>
	 * @param ipv4 表示IPv4地址的4个字节
	 * @return IPv4-mapped IPv6 Address bytes
	 */
	private byte[] mappingToIPv6(byte[] ipv4) {
		byte[] ipv6 = new byte[16];
		System.arraycopy(ipv4, 0, ipv6, 12, 4);
		System.arraycopy(prefix, 0, ipv6, 0, 12);
		return ipv6;
	}
}
