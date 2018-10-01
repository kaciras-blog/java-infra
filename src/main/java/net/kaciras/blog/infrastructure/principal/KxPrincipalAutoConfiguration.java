package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
		public ServletPrincipalFilter servletPrincipalFilter() {
			return new ServletPrincipalFilter(properties);
		}
	}

	@ConditionalOnClass(DispatcherHandler.class)
	@Configuration
	protected class WebFluxPrincipalConfiguration {

		@Bean
		@Order(Ordered.LOWEST_PRECEDENCE - 40)
		public ReactivePrincipalFilter reactivePrincipalFilter() {
			return new ReactivePrincipalFilter(properties);
		}
	}

	@Bean
	@ConditionalOnBean(name = "loadTimeWeaver")
	public PrincipalAspect principalAspect() {
		return new PrincipalAspect();
	}
}
