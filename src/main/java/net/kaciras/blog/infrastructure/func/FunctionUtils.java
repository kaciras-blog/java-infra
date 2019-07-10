package net.kaciras.blog.infrastructure.func;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 工具类，直接静态引入：
 * import static net.kaciras.blog.infrastructure.func.FunctionUtils.*;
 * <p>
 * 然后可以 stream.map(unchecked(throwingFunction))，避免显示转换写一堆泛型参数的麻烦。
 */
public final class FunctionUtils {

	public static <T> Consumer<T> unchecked(UncheckedConsumer<T> consumer) {
		return consumer;
	}

	public static Runnable unchecked(UncheckedRunnable runnable) {
		return runnable;
	}

	public static <T, R> Function<T, R> unchecked(UncheckedFunction<T, R> function) {
		return function;
	}
}
