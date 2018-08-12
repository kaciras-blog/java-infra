package net.kaciras.blog.infrastructure.exception;

public class ResourceNotFoundException extends WebBusinessException {

	public ResourceNotFoundException() {
		this("找不到所请求的资源");
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceNotFoundException(Throwable cause) {
		this("找不到所请求的资源", cause);
	}

	public ResourceNotFoundException(String message) { super(message); }

	@Override
	public int statusCode() {
		return 404;
	}
}
