package net.kaciras.blog.infrastructure.func;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * some helper method for lambda expression.
 */
@SuppressWarnings("unchecked")
public final class Lambdas {

	private static Runnable RUNNABLE = () -> { };
	private static Consumer CONSUMER = t -> { };
	private static Function KEEP_INTACT = t -> t;

	/**
	 * 返回一个Runnable对象，它不执行任何操作。
	 *
	 * @return 一个Runnable实例
	 */
	public static Runnable nopRunnable() {
		return RUNNABLE;
	}

	/**
	 * 返回一个Consumer对象，它能够接收任意值，但不执行任何操作。
	 *
	 * @return 一个Consumer实例
	 */
	public static <T> Consumer<T> nopConsumer() {
		return CONSUMER;
	}

	/**
	 * 返回一个Function对象，它能够接收任意值，并原封不动地返回接收的值。
	 *
	 * @return 一个Function实例
	 */
	public static <T> Function<T, T> keepIntact() {
		return KEEP_INTACT;
	}

	/**
	 * 将能够抛出未检查异常的方法包装为只抛出运行时异常的方法。原始的异常将
	 * 被包装成RuntimeException抛出。
	 *
	 * @param origin 原方法
	 * @param <T> 原方法入参类型
	 * @param <R> 原方法返回值类型
	 * @return 包装后的方法
	 */
	public static <T, R> Function<T, R> check(UncheckedFunction<T, R> origin) {
		return check(origin, e -> { throw new RuntimeException(e); });
	}

	public static <T, R, E extends Throwable> Function<T, R> check(
			UncheckedFunction<T, R> origin, Function<E, R> onError) {
		return arg -> {
			try {
				return origin.apply(arg);
			} catch (Exception e) {
				return onError.apply((E) e);
			}
		};
	}

	/**
	 * this is a 'static' class can not be instance.
	 */
	private Lambdas() {}
}
