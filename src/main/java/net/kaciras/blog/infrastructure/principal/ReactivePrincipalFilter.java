package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.lang.NonNull;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Optional;

/**
 * 从会话中查询用户的身份，作为ServerRequest中的Principal属性。无论用户是否
 * 登录，都将返回一个Principal。
 */
@Order(10_000)
@Slf4j
@RequiredArgsConstructor
@Setter
public final class ReactivePrincipalFilter implements WebFilter {

	private final Domain globalDomain;

	private String cookieName;
	private String headerName;
	private String parameterName;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return chain.filter(new IMWebExchange(exchange));
	}

	private void changeCsrfCookie(ServerWebExchange exchange) {
		var oldCookie = exchange.getRequest().getCookies().getFirst(cookieName);
//		ResponseCookie.from(oldCookie.getName(), UUID.randomUUID().toString())
//				.domain()
	}

	/**
	 * 封装默认的请求对象，重写getPrincipal()方法使其返回自定义的Principal。
	 */
	private final class IMWebExchange extends ServerWebExchangeDecorator {

		private IMWebExchange(ServerWebExchange delegate) {
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
		private WebPrincipal doGetPrincipal(@NonNull WebSession session) {
			var userId = session.getAttribute("UserId");
			if (userId != null && checkCSRF()) {
				return new WebPrincipal((Integer) userId);
			}
			return WebPrincipal.ANONYMOUS;
		}

		private boolean checkCSRF() {
			if(cookieName == null) {
				return true;
			}
			var request = getRequest();
			var nullable = Optional.ofNullable(request.getCookies().getFirst(cookieName)).map(HttpCookie::getValue);

			if (headerName != null) {
				nullable = nullable.filter(token -> token.equals(request.getHeaders().getFirst(headerName)));
			}
			if (parameterName != null) {
				nullable = nullable.filter(token -> token.equals(request.getQueryParams().getFirst(parameterName)));
			}
			return nullable.isPresent();
		}
	}
}
