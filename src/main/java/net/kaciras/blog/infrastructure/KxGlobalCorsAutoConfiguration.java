package net.kaciras.blog.infrastructure;

import lombok.RequiredArgsConstructor;
import net.kaciras.blog.infrastructure.CorsProperties.CorsTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@EnableConfigurationProperties(CorsProperties.class)
@Configuration
@RequiredArgsConstructor
public class KxGlobalCorsAutoConfiguration {

	private final CorsProperties properties;

	private CorsConfiguration getCorsConfig() {
		var config = new CorsConfiguration();
		config.setAllowCredentials(true);

		if (properties.getTemplate() == CorsTemplate.Default) {
			config.applyPermitDefaultValues();
		} else if (properties.getTemplate() == CorsTemplate.AllowAll) {
			var all = List.of(CorsConfiguration.ALL);
			config.setAllowedMethods(all);
			config.setAllowedOrigins(all);
			config.setAllowedHeaders(all);
			config.setExposedHeaders(all);
		}

		if (properties.getOrigins() != null) {
			config.setAllowedOrigins(List.of(properties.getOrigins()));
		}
		if (properties.getAllowHeaders() != null) {
			config.setAllowedHeaders(List.of(properties.getAllowHeaders()));
		}
		if (properties.getExposedHeaders() != null) {
			config.setExposedHeaders(List.of(properties.getExposedHeaders()));
		}
		if (properties.getMaxAge() != null) {
			config.setMaxAge(properties.getMaxAge());
		}

		config.addAllowedOrigin("http://localhost");
		config.addAllowedOrigin("https://localhost");
		return config;
	}

	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	@Configuration
	class ServletSessionConfiguration {

		@Bean
		public CorsFilter corsFilter() {
			var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", getCorsConfig());
			return new CorsFilter(source);
		}
	}

	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	@Configuration
	class ReactiveSessionConfiguration {

		@Bean
		public CorsWebFilter corsFilter() {
			var source = new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", getCorsConfig());
			return new CorsWebFilter(source);
		}
	}
}
