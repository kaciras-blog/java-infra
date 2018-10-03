package net.kaciras.blog.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

import javax.servlet.Filter;

@RequiredArgsConstructor
@EnableConfigurationProperties(DevelopmentProperties.class)
@Configuration
public class DevelopmentAutoConfiguration {

	private final DevelopmentProperties properties;

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void sleepIgnoreInterrupt(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.interrupted();
		}
	}

	/**
	 * Servlet 环境下的配置，将启用基于 Servlet 技术栈的组件。
	 */
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	@Configuration
	protected class MvcDevelopmentConfiguration {

		// WARN：同步阻塞！
		@ConditionalOnProperty("development.delay")
		@Bean
		public Filter delayFilter() {
			return (req, res, chain) -> {
				sleepIgnoreInterrupt(properties.getDelay().toMillis());
				chain.doFilter(req, res);
			};
		}
	}

	/**
	 * Spring Webflux 环境下的配置，将启用基于 Spring Webflux 技术栈的组件。
	 */
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	@Configuration
	protected class WebFluxDevelopmentConfiguration {

		@ConditionalOnProperty("development.delay")
		@Bean
		public WebFilter delayFilter() {
			return (exchange, chain) -> chain.filter(exchange).delaySubscription(properties.getDelay());
		}
	}
}
