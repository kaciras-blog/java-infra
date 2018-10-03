package net.kaciras.blog.infrastructure.principal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kaciras.authorization")
@Data
public final class AuthorizationProperties {

	private boolean securityContext = false;

	private boolean csrfVerify = true;

	private String csrfSessionName = "CSRF-Token";

	private String csrfHeaderName = "X-CSRF-Token";
}
