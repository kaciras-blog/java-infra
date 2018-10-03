package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * 从会话中查询用户的身份，作为ServerRequest中的Principal属性。无论用户是否
 * 登录，都将返回一个Principal。
 */
@Slf4j
@RequiredArgsConstructor
@Order(1024)
final class ReactivePrincipalFilter implements WebFilter {

	private final AuthorizationProperties properties;
	private final Domain globalDomain;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return chain.filter(new IMWebExchange(exchange));
	}

	/**
	 * 封装默认的请求对象，重写getPrincipal()方法使其返回自定义的Principal。
	 */
	private final class IMWebExchange extends ServerWebExchangeDecorator {

		IMWebExchange(ServerWebExchange delegate) {
			super(delegate);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Principal> Mono<T> getPrincipal() {
			return (Mono<T>) getDelegate()
					.getSession()
					.map(this::doGetPrincipal)
					.map(globalDomain::enter);
		}

		/**
		 * 从会话中查询用户的身份，只有在getPrincipal().subscribe()时才会调用。
		 *
		 * @param session 会话
		 * @return 用户的身份
		 */
		private WebPrincipal doGetPrincipal(WebSession session) {
			var id = session.getAttribute("UserId");
			if (id == null) {
				return new WebPrincipal(0);
			}
			if (!properties.isCsrfVerify()) {
				return new WebPrincipal((int) id);
			}

			var csrf = session.getAttribute(properties.getCsrfSessionName());
			var header = getDelegate().getRequest().getHeaders().get(properties.getCsrfHeaderName());

			if(csrf != null && csrf.equals(header)) {
				return new WebPrincipal((int) id);
			}
			logger.debug("CSRF check failed, expect:" + csrf + ", but got:" + header);
			return new WebPrincipal(0);
		}
	}
}
