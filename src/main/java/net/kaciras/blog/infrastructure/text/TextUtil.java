package net.kaciras.blog.infrastructure.text;

import lombok.extern.slf4j.Slf4j;
import net.kaciras.text.STConverter;
import net.kaciras.text.SensitiveWordDetector;
import net.kaciras.text.SkipableAhoCorasick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public final class TextUtil {

	private static final SensitiveWordDetector swd = new SensitiveWordDetector(new SkipableAhoCorasick());
	private static final STConverter stConverter;

	static {
		swd.getMatcher().addStopChars(" _-~!@#$%&*()[]{},.，。、");
		loadSensitiveWords("sensitive/Porn.txt");
		loadSensitiveWords("sensitive/Political.txt");

		try {
			stConverter = new STConverter();
		} catch (IOException e) {
			throw new RuntimeException("繁简转换词库加载失败");
		}

		logger.debug("加载了内置的词库");
	}

	private static void loadSensitiveWords(String name) {
		try{
			InputStream stream = SensitiveWordDetector.class.getClassLoader().getResourceAsStream(name);
			InputStreamReader insr = new InputStreamReader(stream, StandardCharsets.UTF_8);

			try (BufferedReader reader = new BufferedReader(insr)) {
				reader.lines()
						.map(String::trim)
						.filter(s -> !s.isEmpty())
						.forEach(swd.getMatcher()::addWords);
			}
		} catch (IOException ex) {
			throw new RuntimeException("敏感词词库加载失败");
		}
	}

	/**
	 * 检查文本中是否含有和谐词。
	 *
	 * @param text 文本
	 * @return true如果有，没有就是false
	 */
	public static boolean isDanger(String text) {
		return !swd.getDangerWords(toSimplified(text)).isEmpty();
	}

	public static String toSimplified(String text) {
		return stConverter.toSimplified(text);
	}

	/**
	 * 估算文本在显示时的行数。
	 *
	 * @param text 文本
	 * @param width 行宽（每行字符数）
	 * @return 行数
	 */
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
