package net.kaciras.blog.infrastructure.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

@ConfigurationProperties("kaciras.authorization")
@Getter
@Setter
public final class AuthorizationProperties {

	private boolean securityContext;

	private boolean dynamicCsrfCookie;

	private String csrfCookie = "CSRF-Token";

	private boolean skipSafeRequest = true;

	/** 如果为null，则不验证请求头 */
	@Nullable
	private String csrfHeader = "X-CSRF-Token";

	/** 如果为null，则不验证请求参数 */
	@Nullable
	private String csrfParameter;

	private boolean adminPrincipal;
}
