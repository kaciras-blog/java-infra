package net.kaciras.blog.infrastructure;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;

final class MiscTest {

	@Test
	void getFirst() {
		var iterable = List.of(11, 13, 17, 19);
		Assertions.assertThat(Misc.getFirst(iterable)).isEqualTo(11);

		Assertions.assertThatThrownBy(() -> Misc.getFirst(List.of()))
				.isInstanceOf(NoSuchElementException.class);
	}

	// 只测了下是否触发警告
	@Test
	void disableIllegalAccessWarning() {
		var backup = System.err;
		var stdout = new ByteArrayOutputStream();
		System.setErr(new PrintStream(stdout));

		Misc.disableIllegalAccessWarning();

		Assertions.assertThat(stdout.size()).isZero();
		System.setErr(backup);
	}
}
