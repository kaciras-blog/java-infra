package net.kaciras.blog.infrastructure.exception;

/**
 * 表示由用户的输入而产生的异常，这些异常通常需要返回信息给前端。
 */
public abstract class WebBusinessException extends RuntimeException {

	public abstract int statusCode();

	WebBusinessException(String message) {
		super(message);
	}

	WebBusinessException(String message, Throwable cause) {
		super(message, cause);
	}
}
