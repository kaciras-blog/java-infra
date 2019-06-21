package net.kaciras.blog.infrastructure.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kaciras.session.cookie")
@Getter
@Setter
public final class SessionCookieProperties {

	private String name = "SESSION";

	private String domain = "localhost";

	private String sameSite;

	private boolean secure;

	private int maxAge = 30 * 24 * 60 * 60;
}