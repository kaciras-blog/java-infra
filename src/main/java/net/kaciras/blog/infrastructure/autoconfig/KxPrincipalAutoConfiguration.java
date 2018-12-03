package net.kaciras.blog.infrastructure.autoconfig;

import lombok.RequiredArgsConstructor;
import net.kaciras.blog.infrastructure.principal.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@EnableConfigurationProperties({AuthorizationProperties.class, DevelopmentProperties.class})
@Configuration
@RequiredArgsConstructor
public class KxPrincipalAutoConfiguration {

	private final AuthorizationProperties authorizationProperties;
	private final DevelopmentProperties developmentProperties;

	/**
	 * Servlet 环境下的配置，将启用基于 Servlet 技术栈的组件。
	 */
	@ConditionalOnWebApplication(type = Type.SERVLET)
	@Configuration
	protected class MvcPrincipalConfiguration {

		@Bean
		public ServletPrincipalFilter servletPrincipalFilter(Domain domain) {
			if (developmentProperties.isAdminPrincipal()) {
				domain = new DevelopAdminDomain(domain);
			}
			return new ServletPrincipalFilter(authorizationProperties, domain);
		}

		@ConditionalOnProperty(name = "kaciras.authorization.security-context", havingValue = "true")
		@Bean
		public ServletSecurityContextFilter securityContextFilter() {
			return new ServletSecurityContextFilter();
		}
	}

	/**
	 * Spring Webflux 环境下的配置，将启用基于 Spring Webflux 技术栈的组件。
	 */
	@ConditionalOnWebApplication(type = Type.REACTIVE)
	@Configuration
	protected class WebFluxPrincipalConfiguration {

		@Bean
		public ReactivePrincipalFilter reactivePrincipalFilter(Domain domain) {
			if (developmentProperties.isAdminPrincipal()) {
				domain = new DevelopAdminDomain(domain);
			}
			return new ReactivePrincipalFilter(authorizationProperties, domain);
		}

		@ConditionalOnProperty(name = "kaciras.authorization.security-context", havingValue = "true")
		@Bean
		WebFilter securityContextFilter() {
			return new ReactiveSecurityContextFilter();
		}
	}

	/**
	 * 注册AOP权限拦截器，可以对一些简单的权限进行拦截。
	 *
	 * @return 切面类
	 * @see AuthorizeAspect
	 * @see RequireAuthorize
	 */
	@Bean
	@ConditionalOnBean(name = "loadTimeWeaver")
	public AuthorizeAspect principalAspect() {
		return new AuthorizeAspect();
	}

	/**
	 * 适配没有注册全局 Domain 的情况。
	 *
	 * @return 一个Domain，原样返回 Principal
	 */
	@Bean
	@ConditionalOnMissingBean(Domain.class)
	public Domain globalDomain() {
		return principal -> principal;
	}
}