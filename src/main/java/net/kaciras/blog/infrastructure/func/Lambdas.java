package net.kaciras.blog.infrastructure.func;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * some helper method for lambda expression.
 */
@SuppressWarnings("unchecked")
@UtilityClass
public class Lambdas {

	private Runnable RUNNABLE = () -> { };
	private Consumer CONSUMER = t -> { };
	private Function KEEP_INTACT = t -> t;

	/**
	 * 返回一个Runnable对象，它不执行任何操作。
	 *
	 * @return 一个Runnable实例
	 */
	public Runnable nopRunnable() {
		return RUNNABLE;
	}

	/**
	 * 返回一个Consumer对象，它能够接收任意值，但不执行任何操作。
	 *
	 * @return 一个Consumer实例
	 */
	public <T> Consumer<T> nopConsumer() {
		return CONSUMER;
	}

	/**
	 * 返回一个Function对象，它能够接收任意值，并原封不动地返回接收的值。
	 *
	 * @return 一个Function实例
	 */
	public <T> Function<T, T> keepIntact() {
		return KEEP_INTACT;
	}
}
