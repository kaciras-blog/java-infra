package net.kaciras.blog.infrastructure.exception;

public class DataTooBigException extends RuntimeException {

	public DataTooBigException() { this("请求所带的数据过长"); }

	public DataTooBigException(String message) {
		super(message);
	}

	public DataTooBigException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataTooBigException(Throwable cause) {
		this("请求所带的数据过长", cause);
	}
}
