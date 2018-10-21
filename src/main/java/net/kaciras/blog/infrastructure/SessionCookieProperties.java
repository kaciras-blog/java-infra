package net.kaciras.blog.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kaciras.spring.session.cookie")
@Data
public final class SessionCookieProperties {

	private String domain = "localhost";

	private String sameSite;

	private boolean secure;

	private int maxAge = 30 * 24 * 60 * 60;
}
