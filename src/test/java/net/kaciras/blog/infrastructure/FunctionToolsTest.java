package net.kaciras.blog.infrastructure;

import net.kaciras.blog.infrastructure.func.UncheckedConsumer;
import net.kaciras.blog.infrastructure.func.UncheckedFunction;
import net.kaciras.blog.infrastructure.func.UncheckedFunctionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class FunctionToolsTest {

	@Test
	void uncheckedConsumer() {
		var holder = new AtomicReference<>();
		Consumer<Object> setter = (UncheckedConsumer<Object>) holder::set;

		setter.accept(FunctionToolsTest.class);
		assertThat(holder.get()).isEqualTo(FunctionToolsTest.class);
	}

	@Test
	void uncheckedConsumerThrows() {
		Consumer<Object> throwing = (UncheckedConsumer<Object>) (t) -> {
			throw new IOException();
		};
		assertThatThrownBy(() -> throwing.accept(null))
				.hasCauseInstanceOf(IOException.class)
				.isInstanceOf(UncheckedFunctionException.class);
	}

	@Test
	void uncheckedFunction() {
		Function<Integer, Integer> cube = (UncheckedFunction<Integer, Integer>) (t) -> t * t * t;
		assertThat(cube.apply(4)).isEqualTo(4 * 4 * 4);
	}

	@Test
	void uncheckedFunctionThrows() {
		Function<Object, Object> throwing = (UncheckedFunction<Object, Object>) (t) -> {
			throw new IOException();
		};
		assertThatThrownBy(() -> throwing.apply(null))
				.hasCauseInstanceOf(IOException.class)
				.isInstanceOf(UncheckedFunctionException.class);
	}
}
