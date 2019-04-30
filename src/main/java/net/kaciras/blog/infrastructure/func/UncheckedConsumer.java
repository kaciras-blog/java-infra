package net.kaciras.blog.infrastructure.func;

import java.util.function.Consumer;

@FunctionalInterface
public interface UncheckedConsumer<T> extends Consumer<T> {

	@Override
	default void accept(T argument) {
		try {
			acceptThrows(argument);
		} catch (Exception e) {
			throw new UncheckedFunctionException(e);
		}
	}

	void acceptThrows(T t) throws Exception;
}