package net.kaciras.blog.infrastructure.func;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * some helper method for lambda expression.
 */
@SuppressWarnings("unchecked")
public final class Lambdas {

	private static Runnable RUNNABLE = () -> { };
	private static Consumer CONSUMER = t -> { };
	private static Function KEEP_INTACT = t -> t;

	public static Runnable nopRunnable() {
		return RUNNABLE;
	}

	public static <T> Consumer<T> nopConsumer() {
		return CONSUMER;
	}

	public static <T> Function<T, T> keepIntact() {
		return KEEP_INTACT;
	}

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
	 * this is a 'static' class and can not be instance.
	 */
	private Lambdas() {}
}
