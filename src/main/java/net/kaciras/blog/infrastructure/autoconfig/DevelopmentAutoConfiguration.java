package net.kaciras.blog.infrastructure.autoconfig;

import lombok.RequiredArgsConstructor;
import net.kaciras.blog.infrastructure.principal.Domain;
import net.kaciras.blog.infrastructure.principal.WebPrincipal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

import javax.servlet.Filter;

/**
 * 帮助开发调试的一些工具，包括请求延时、关闭身份验证等。
 */
@RequiredArgsConstructor
@ConditionalOnClass
@EnableConfigurationProperties(DevelopmentProperties.class)
@Configuration
public class DevelopmentAutoConfiguration {

	private final DevelopmentProperties properties;

	/**
	 * 开发管理员领域？没想到个好名字。
	 * 开发测试用，将匿名用户转换为管理员，便于调试。
	 */
	@ConditionalOnProperty(name = "kaciras.development.admin-principal", havingValue = "true")
	@Bean
	public Domain developAdminDomain() {
		return ignore -> new WebPrincipal(WebPrincipal.ADMIN_ID);
	}

	/**
	 * Servlet 环境下的配置，将启用基于 Servlet 技术栈的组件。
	 */
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	@Configuration
	protected class MvcDevelopmentConfiguration {

		@SuppressWarnings("ResultOfMethodCallIgnored")
		@ConditionalOnProperty("development.httpDelay")
		@Bean
		public Filter delayFilter() {
			var millis = properties.getHttpDelay().toMillis();
			return (request, response, chain) -> {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
				chain.doFilter(request, response);
			};
		}
	}

	/**
	 * Spring Webflux 环境下的配置，将启用基于 Spring Webflux 技术栈的组件。
	 */
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	@Configuration
	protected class WebFluxDevelopmentConfiguration {

		@ConditionalOnProperty("development.httpDelay")
		@Bean
		public WebFilter delayFilter() {
			return (exchange, chain) -> chain.filter(exchange).delaySubscription(properties.getHttpDelay());
		}
	}
}
