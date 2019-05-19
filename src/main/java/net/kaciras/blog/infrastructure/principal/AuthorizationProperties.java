package net.kaciras.blog.infrastructure.principal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

@ConfigurationProperties("kaciras.authorization")
@Data
public final class AuthorizationProperties {

	private boolean securityContext;

	private boolean dynamicCsrfCookie;

	private String csrfCookieName = "CSRF-Token";

	/** 如果为null，则不验证请求头 */
	@Nullable
	private String csrfHeaderName = "X-CSRF-Token";

	/** 如果为null，则不验证请求参数 */
	@Nullable
	private String csrfParameterName;
}
