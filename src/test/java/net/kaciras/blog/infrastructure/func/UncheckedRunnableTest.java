package net.kaciras.blog.infrastructure.func;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static net.kaciras.blog.infrastructure.func.FunctionUtils.unchecked;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class UncheckedRunnableTest {

	@Test
	void noThrows() {
		var holder = new AtomicReference<>();
		Runnable setting = (UncheckedRunnable) () -> holder.set(UncheckedRunnable.class);

		setting.run();
		assertThat(holder.get()).isEqualTo(UncheckedRunnable.class);
	}

	@Test
	void doThrows() {
		Runnable runnable = (UncheckedRunnable) () -> {
			throw new IOException();
		};
		assertThatThrownBy(runnable::run)
				.isInstanceOf(UncheckedFunctionException.class)
				.hasCauseInstanceOf(IOException.class);
	}

	@Test
	void uncheckedHelpers() {
		Runnable runnable = unchecked(() -> { throw new IOException(); });
		assertThat(runnable).isInstanceOf(UncheckedRunnable.class);
	}
}
