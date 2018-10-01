package net.kaciras.blog.infrastructure.principal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kaciras.authorization")
@Data
final class AuthorizationProperties {

	private boolean csrfVerify;

	private boolean debugMode;

	private String csrfSessionName = "CSRF-Token";

	private String csrfHeaderName = "X-CSRF-Token";
}
