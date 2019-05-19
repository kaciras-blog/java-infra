package net.kaciras.blog.infrastructure.principal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

@ConfigurationProperties("kaciras.authorization")
@Data
public final class AuthorizationProperties {

	private boolean securityContext;

	private boolean csrfVerify = true;

	private boolean dynamicCsrfCookie;

	private String csrfCookieName = "CSRF-Token";

	@Nullable
	private String csrfHeaderName = "X-CSRF-Token";

	@Nullable
	private String csrfParameterName = null;
}
