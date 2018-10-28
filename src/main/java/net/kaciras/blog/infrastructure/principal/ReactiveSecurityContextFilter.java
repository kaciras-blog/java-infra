package net.kaciras.blog.infrastructure.principal;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public final class ReactiveSecurityContextFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return exchange.<WebPrincipal>getPrincipal()
				.doOnNext(SecurityContext::setPrincipal)
				.flatMap(p -> chain.filter(exchange));
	}
}
