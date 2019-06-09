package net.kaciras.blog.infrastructure;

import net.kaciras.blog.infrastructure.func.UncheckedConsumer;
import net.kaciras.blog.infrastructure.func.UncheckedFunction;
import net.kaciras.blog.infrastructure.func.UncheckedFunctionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

final class FunctionToolsTest {

	@Test
	void uncheckedConsumer() {
		var holder = new AtomicReference<>();
		UncheckedConsumer<Object> throwing = holder::set;

		throwing.accept(FunctionToolsTest.class);
		Assertions.assertThat(holder.get()).isEqualTo(FunctionToolsTest.class);
	}

	@Test
	void uncheckedConsumerThrows() {
		UncheckedConsumer<Object> throwing = (t) -> {
			throw new IOException();
		};
		Assertions.assertThatThrownBy(() -> throwing.accept(null))
				.hasCauseInstanceOf(IOException.class)
				.isInstanceOf(UncheckedFunctionException.class);
	}

	@Test
	void uncheckedFunction() {
		UncheckedFunction<Integer, Integer> function = (t) -> t * t * t;
		Assertions.assertThat(function.apply(4)).isEqualTo(4 * 4 * 4);
	}

	@Test
	void uncheckedFunctionThrows() {
		UncheckedFunction<Object, Object> throwing = (t) -> {
			throw new IOException();
		};
		Assertions.assertThatThrownBy(() -> throwing.apply(null))
				.hasCauseInstanceOf(IOException.class)
				.isInstanceOf(UncheckedFunctionException.class);
	}
}
