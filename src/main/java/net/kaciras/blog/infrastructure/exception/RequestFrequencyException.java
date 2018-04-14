package net.kaciras.blog.infrastructure.exception;

public class RequestFrequencyException extends RuntimeException {

	public RequestFrequencyException() { this("操作频率过高，请稍后再试"); }

	public RequestFrequencyException(String message) {
		super(message);
	}

	public RequestFrequencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestFrequencyException(Throwable cause) {
		this("操作频率过高，请稍后再试", cause);
	}
}
