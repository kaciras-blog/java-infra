package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@RequiredArgsConstructor
public class KxPrincipalAutoConfiguration {

	private final AuthorizationProperties properties;

	@ConditionalOnClass(DispatcherServlet.class)
	@Bean
	ServletPrincipalFilter servletPrincipalFilter() {
		return new ServletPrincipalFilter(properties);
	}

	@ConditionalOnClass(DispatcherHandler.class)
	@Bean
	ReactivePrincipalFilter reactivePrincipalFilter() {
		return new ReactivePrincipalFilter(properties);
	}
}
