package net.kaciras.blog.infrastructure.bootstarp;

import net.kaciras.blog.infrastructure.TextUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TextUtilsTest {

	@Test
	void test() {
		Assertions.assertThat(TextUtils.isDanger("法轮功")).isTrue();
	}
}
