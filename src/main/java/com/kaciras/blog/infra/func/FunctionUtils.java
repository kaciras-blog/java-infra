package com.kaciras.blog.infra.func;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 工具类，直接静态引入：
 * import static com.kaciras.blog.infrastructure.func.FunctionUtils.*;
 * <p>
 * 然后可以 stream.map(unchecked(throwingFunction))，避免显示转换写一堆泛型参数的麻烦。
 */
public final class FunctionUtils {

	public static <T> Consumer<T> unchecked(UncheckedConsumer<T> consumer) {
		return consumer;
	}

	public static <T, U> BiConsumer<T, U> unchecked(UncheckedBiConsumer<T, U> consumer) {
		return consumer;
	}

	public static <T, R> Function<T, R> unchecked(UncheckedFunction<T, R> function) {
		return function;
	}

	public static <T, U, R> BiFunction<T, U, R> unchecked(UncheckedBiFunction<T, U, R> function) {
		return function;
	}

	public static Runnable unchecked(UncheckedRunnable runnable) {
		return runnable;
	}
}
