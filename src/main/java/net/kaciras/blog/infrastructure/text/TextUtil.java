package net.kaciras.blog.infrastructure.text;

public final class TextUtil {

	public static boolean isDanger(String text) {
		return false;
	}

	public static int getHeight(String text, int width) {
		char[] chars = text.toCharArray();
		int length = 0;
		for (char ch : chars) {
			if (ch > 0x7F) {
				length += 2; //大于128的（中文等）占2位置
				continue;
			}
			switch (ch) {
				case '\n':
					length += width;
					continue;
				case '\t':
					length += 8; //一个Tab占8个位置
					continue;
			}
			length++;
		}
		return length / width + 1;
	}

	private TextUtil() {}
}
