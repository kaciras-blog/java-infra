package net.kaciras.blog.infrastructure.text;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class STConvertorTest {

	private STConverter stConverter;

	@BeforeEach
	void setUp() throws Exception {
		stConverter = new STConverter();

		/* add custom mapping */
		stConverter.addMapping("繁体中文", "正體中文");
	}

	@Test
	void testWord()  {
		Assertions.assertEquals("簡單", stConverter.toTraditional("简单"));
		Assertions.assertEquals("正體中文", stConverter.toTraditional("繁体中文"));

		/* test removing */
		Assertions.assertFalse(stConverter.removeSimpleMapping("繁体中"));
		Assertions.assertTrue(stConverter.removeSimpleMapping("繁体中文"));
		Assertions.assertFalse(stConverter.removeSimpleMapping("繁体中文"));

		Assertions.assertEquals("繁體中文", stConverter.toTraditional("繁体中文"));
	}

	@Test
	void testSentence() {
		String simp = "繁体中文转换为简体中文很简单";
		String trad = "正體中文轉換爲簡體中文很簡單";
		Assertions.assertEquals(trad, stConverter.toTraditional(simp));
		Assertions.assertEquals(simp, stConverter.toSimplified(trad));
	}
}
