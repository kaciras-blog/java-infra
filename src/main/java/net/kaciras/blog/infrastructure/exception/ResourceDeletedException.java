package net.kaciras.blog.infrastructure.exception;

public class ResourceDeletedException extends WebBusinessException {

	public ResourceDeletedException() {
		this("请求的资源已经被删除");
	}

	public ResourceDeletedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceDeletedException(Throwable cause) {
		this("请求的资源已经被删除", cause);
	}

	public ResourceDeletedException(String message) { super(message);}

	@Override
	public int statusCode() {
		return 410;
	}
}
