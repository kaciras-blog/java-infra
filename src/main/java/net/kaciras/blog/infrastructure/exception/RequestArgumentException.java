package net.kaciras.blog.infrastructure.exception;

public class RequestArgumentException extends RuntimeException {

	public RequestArgumentException() {
		this("请求中含有不合法的数据");
	}

	public RequestArgumentException(String message) {
		super(message);
	}

	public RequestArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestArgumentException(Throwable cause) {
		this("请求中含有不合法的数据", cause);
	}
}
