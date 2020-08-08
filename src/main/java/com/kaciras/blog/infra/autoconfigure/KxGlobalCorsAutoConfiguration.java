package com.kaciras.blog.infra.autoconfigure;

import com.kaciras.blog.infra.autoconfigure.CorsProperties.CorsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * 配置全局的CORS处理，该类使用CorsFilter拦截器，有别于WebConfigurer中的配置。
 * 该类将检查 kaciras.cors 空间下的配置。
 */
@EnableConfigurationProperties(CorsProperties.class)
@Configuration(proxyBeanMethods = false)
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
		}

		if (properties.getMaxAge() != null) {
			config.setMaxAge(properties.getMaxAge());
		}

		if (properties.getAllowedOrigins() != null) {
			config.setAllowedOrigins(properties.getAllowedOrigins());
		}
		if (properties.getAllowedMethods() != null) {
			config.setAllowedMethods(properties.getAllowedMethods());
		}
		if (properties.getAllowedHeaders() != null) {
			config.setAllowedHeaders(properties.getAllowedHeaders());
		}
		if (properties.getExposedHeaders() != null) {
			config.setExposedHeaders(properties.getExposedHeaders());
		}

		return config;
	}

	// 因为要设置优先级所以使用了 FilterRegistrationBean 来包装
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", getCorsConfig());

		var registration = new FilterRegistrationBean<CorsFilter>();
		registration.setOrder(FILTER_ORDER);
		registration.setFilter(new CorsFilter(source));
		return registration;
	}
}
