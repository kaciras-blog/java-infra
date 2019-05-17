package net.kaciras.blog.infrastructure.autoconfig;

import lombok.RequiredArgsConstructor;
import net.kaciras.blog.infrastructure.autoconfig.CorsProperties.CorsTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * 配置全局的CORS处理，该类使用CorsFilter拦截器，有别于WebConfigurer中的配置。
 * 该类将检查 kaciras.cors 空间下的配置。
 */
@EnableConfigurationProperties(CorsProperties.class)
@Configuration
@RequiredArgsConstructor
public class KxGlobalCorsAutoConfiguration {

	// 尽量提早过滤掉无效的请求
	private static final int FILTER_ORDER = Integer.MIN_VALUE + 10;

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
			config.setAllowedOrigins(properties.getOrigins());
		}
		if (properties.getMethods() != null) {
			config.setAllowedMethods(properties.getMethods());
		}
		if (properties.getAllowHeaders() != null) {
			config.setAllowedHeaders(properties.getAllowHeaders());
		}
		if (properties.getExposedHeaders() != null) {
			config.setExposedHeaders(properties.getExposedHeaders());
		}
		if (properties.getMaxAge() != null) {
			config.setMaxAge(properties.getMaxAge());
		}
		return config;
	}

	@ConditionalOnWebApplication(type = Type.SERVLET)
	@Configuration
	class ServletSessionConfiguration {

		@Bean
		public FilterRegistrationBean<CorsFilter> corsFilter() {
			var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", getCorsConfig());

			var registration = new FilterRegistrationBean<CorsFilter>();
			registration.setOrder(FILTER_ORDER);
			registration.setFilter(new CorsFilter(source));
			return registration;
		}
	}

	@ConditionalOnWebApplication(type = Type.REACTIVE)
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
