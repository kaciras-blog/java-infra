package net.kaciras.blog.infrastructure.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("kaciras.cors")
@Data
public final class CorsProperties {

	private CorsTemplate template;

	private List<String> origins;

	private List<String> methods;

	private List<String> allowHeaders;

	private List<String> exposedHeaders;

	private Long maxAge;

	public enum CorsTemplate {
		Default,
		AllowAll
	}
}
