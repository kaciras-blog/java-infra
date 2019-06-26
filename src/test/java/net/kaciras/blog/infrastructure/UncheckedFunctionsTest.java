package net.kaciras.blog.infrastructure;

import net.kaciras.blog.infrastructure.func.UncheckedConsumer;
import net.kaciras.blog.infrastructure.func.UncheckedFunction;
import net.kaciras.blog.infrastructure.func.UncheckedFunctionException;
import net.kaciras.blog.infrastructure.func.UncheckedRunnable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class UncheckedFunctionsTest {

	@Test
	void consumer() {
		var holder = new AtomicReference<>();
		Consumer<Object> setter = (UncheckedConsumer<Object>) holder::set;

		setter.accept(UncheckedFunctionsTest.class);
		assertThat(holder.get()).isEqualTo(UncheckedFunctionsTest.class);
	}

	@Test
	void consumerThrows() {
		Consumer<Object> throwing = (UncheckedConsumer<Object>) (t) -> {
			throw new IOException();
		};
		assertThatThrownBy(() -> throwing.accept(null))
				.isInstanceOf(UncheckedFunctionException.class)
				.hasCauseInstanceOf(IOException.class);
	}

	@Test
	void function() {
		Function<Integer, Integer> cube = (UncheckedFunction<Integer, Integer>) (t) -> t * t * t;
		assertThat(cube.apply(4)).isEqualTo(4 * 4 * 4);
	}

	@Test
	void functionThrows() {
		Function<Object, Object> throwing = (UncheckedFunction<Object, Object>) (t) -> {
			throw new IOException();
		};
		assertThatThrownBy(() -> throwing.apply(null))
				.isInstanceOf(UncheckedFunctionException.class)
				.hasCauseInstanceOf(IOException.class);
	}

	@Test
	void runnable() {
		var holder = new AtomicReference<>();
		Runnable setting = (UncheckedRunnable) () -> holder.set(UncheckedRunnable.class);

		setting.run();
		assertThat(holder.get()).isEqualTo(UncheckedRunnable.class);
	}

	@Test
	void runnableThrows() {
		Runnable runnable = (UncheckedRunnable) () -> {
			throw new IOException();
		};
		assertThatThrownBy(runnable::run)
				.isInstanceOf(UncheckedFunctionException.class)
				.hasCauseInstanceOf(IOException.class);
	}
}
