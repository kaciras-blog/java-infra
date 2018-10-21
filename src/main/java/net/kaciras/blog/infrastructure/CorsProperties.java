package net.kaciras.blog.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kaciras.cors")
@Data
public final class CorsProperties {

	private CorsTemplate template;

	private String[] origins;

	private String[] allowHeaders;

	private String[] exposedHeaders;

	private Long maxAge;

	public enum CorsTemplate {
		Default,
		AllowAll
	}
}
