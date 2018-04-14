package net.kaciras.blog.infrastructure.codec;

public final class CodecUtils {

	private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

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

	private CodecUtils() {}
}
