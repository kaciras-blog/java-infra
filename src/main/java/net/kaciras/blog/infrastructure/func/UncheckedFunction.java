package net.kaciras.blog.infrastructure.func;

public interface UncheckedFunction<T, R> {

	R apply(T value) throws Exception;
}
