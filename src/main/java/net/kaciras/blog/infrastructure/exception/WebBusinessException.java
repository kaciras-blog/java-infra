package net.kaciras.blog.infrastructure.exception;

public abstract class WebBusinessException extends RuntimeException {

	public abstract int statusCode();

	public WebBusinessException(String message) {
		super(message);
	}

	public WebBusinessException(String message, Throwable cause) {
		super(message, cause);
	}
}
