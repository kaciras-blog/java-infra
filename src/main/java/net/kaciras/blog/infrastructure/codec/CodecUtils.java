package net.kaciras.blog.infrastructure.codec;

import java.net.Inet6Address;
import java.net.InetAddress;

public final class CodecUtils {

	private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private static final byte[] prefix = new byte[12];

	static { prefix[10] = prefix[11] = (byte)0xFF; }

	public static String encodeHex(byte[] bytes, int offset, int length) {
		char[] out = new char[length << 1];
		for (int i = offset, j = 0; i < offset + length; i++) {
			out[j++] = DIGITS[(0xF0 & bytes[i]) >>> 4];
			out[j++] = DIGITS[0x0F & bytes[i]];
		}
		return new String(out);
	}

	public static String encodeHex(byte[] bytes) {
		return encodeHex(bytes, 0, bytes.length);
	}

	public static byte[] decodeHex(String text) {
		return decodeHex(new byte[text.length() >> 1], 0, text);
	}

	public static byte[] decodeHex(byte[] target, int offset, String text) {
		char[] data = text.toCharArray();

		if ((data.length & 1) != 0) {
			throw new IllegalArgumentException("Hex字符串长度不是偶数");
		}

		for (int i = offset, j = 0; j < data.length; i++, j++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			target[i] = (byte) (f & 0xFF);
		}
		return target;
	}

	private static int toDigit(char ch, int index) {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new IllegalArgumentException("位置" + index + "出现非Hex字符" + ch);
		}
		return digit;
	}

	public static int indexOfBytes(byte[] bytes, byte[] part, int start) {
		int len = bytes.length - part.length + 1;
		for (int i = start; i < len; ++i) {
			boolean found = true;
			for (int j = 0; j < part.length; ++j) {
				if (bytes[i + j] != part[j]) { found = false; break; }
			}
			if (found) return i;
		}
		return -1;
	}

	/**
	 * 把InetAddress转换为16字节的数组
	 *
	 * @param address 地址
	 * @return 字节数组
	 */
	public static byte[] toIPv6Address(InetAddress address) {
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
	private static byte[] mappingToIPv6(byte[] ipv4) {
		byte[] ipv6 = new byte[16];
		System.arraycopy(ipv4, 0, ipv6, 12, 4);
		System.arraycopy(prefix, 0, ipv6, 0, 12);
		return ipv6;
	}

	private CodecUtils() {}
}
