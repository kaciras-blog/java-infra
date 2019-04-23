package net.kaciras.blog.infrastructure.func;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends Function<T,R> {

	@Override
	default R apply(T argument) {
		try {
			return applyThrows(argument);
		} catch (Exception e) {
			throw new UncheckedFunctionException(e);
		}
	}

	R applyThrows(T t) throws Exception;
}
