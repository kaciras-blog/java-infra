package net.kaciras.blog.infrastructure.func;

@FunctionalInterface
public interface UncheckedFunction<T, R> {

	R apply(T value) throws Exception;
}
