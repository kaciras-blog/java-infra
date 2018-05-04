package net.kaciras.blog.infrastructure.bootstarp;

import net.kaciras.blog.infrastructure.text.TextUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TextUtilsTest {

	@Test
	void test() {
		Assertions.assertThat(TextUtil.isDanger("法轮功")).isTrue();
	}
}
