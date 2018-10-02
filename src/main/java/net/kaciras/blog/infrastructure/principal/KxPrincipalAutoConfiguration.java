package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.servlet.DispatcherServlet;

@EnableConfigurationProperties(AuthorizationProperties.class)
@Configuration
@RequiredArgsConstructor
public class KxPrincipalAutoConfiguration {

	private final AuthorizationProperties properties;

	@ConditionalOnClass(DispatcherServlet.class)
	@Configuration
	protected class MvcPrincipalConfiguration {

		@Bean
		@Order(Ordered.LOWEST_PRECEDENCE - 40)
		public ServletPrincipalFilter servletPrincipalFilter(Domain domain) {
			return new ServletPrincipalFilter(properties, domain);
		}

		@ConditionalOnProperty(name = "kaciras.authorization.security-context", havingValue = "true")
		@Bean
		public ServletSecurityContextFilter securityContextFilter() {
			return new ServletSecurityContextFilter();
		}
	}

	@ConditionalOnClass(DispatcherHandler.class)
	@Configuration
	protected class WebFluxPrincipalConfiguration {

		@Bean
		@Order(Ordered.LOWEST_PRECEDENCE - 40)
		public ReactivePrincipalFilter reactivePrincipalFilter(Domain domain) {
			return new ReactivePrincipalFilter(properties, domain);
		}
	}

	@Bean
	@ConditionalOnBean(name = "loadTimeWeaver")
	public AuthorizeAspect principalAspect() {
		return new AuthorizeAspect();
	}

	@Bean
	@ConditionalOnMissingBean(Domain.class)
	public Domain globalDomain() {
		return principal -> principal;
	}
}
