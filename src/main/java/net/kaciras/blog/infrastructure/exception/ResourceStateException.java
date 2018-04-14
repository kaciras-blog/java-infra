package net.kaciras.blog.infrastructure.exception;

/**
 * 在当前资源集合的状态下，无法完成请求的操作。
 */
public class ResourceStateException extends RuntimeException {

	public ResourceStateException() {
		this("资源的状态不允许执行请求的操作");
	}

	public ResourceStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceStateException(Throwable cause) {
		this("资源的状态不允许执行请求的操作", cause);
	}

	public ResourceStateException(String message) { super(message); }
}
