package net.kaciras.blog.infrastructure.exception;

public class PermissionException extends RuntimeException {

	public PermissionException() { this("你没有权限执行这个操作"); }

	public PermissionException(String message) {
		super(message);
	}

	public PermissionException(String message, Throwable cause) {
		super(message, cause);
	}

	public PermissionException(Throwable cause) {
		this("你没有权限执行这个操作", cause);
	}
}
