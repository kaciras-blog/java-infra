package net.kaciras.blog.infrastructure.exception;

public class LegallyProhibitedException extends WebBusinessException {

	public LegallyProhibitedException() {
		super("请求的操作被和谐了");
	}

	public LegallyProhibitedException(Throwable cause) {
		super("请求的操作被和谐了", cause);
	}

	public LegallyProhibitedException(String message) {
		super(message);
	}

	public LegallyProhibitedException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public int statusCode() {
		return 451;
	}
}
