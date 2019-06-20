package net.kaciras.blog.infrastructure.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("kaciras.development")
@Getter
@Setter
public class DevelopmentProperties {

	private boolean adminPrincipal;

	private Duration httpDelay;
}
