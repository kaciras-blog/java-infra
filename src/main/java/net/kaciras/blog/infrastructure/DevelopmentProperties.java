package net.kaciras.blog.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("development")
@Data
public class DevelopmentProperties {

	private boolean adminPrincipal;

	private Duration delay;
}
