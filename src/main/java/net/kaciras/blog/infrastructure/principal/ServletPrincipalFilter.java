package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.kaciras.blog.infrastructure.Misc;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Order(10_000)
@Slf4j
@RequiredArgsConstructor
@Setter
public final class ServletPrincipalFilter extends HttpFilter {

	private final Domain globalDomain;

	private String domain;
	private boolean dynamicToken;

	private String cookieName;
	private String headerName;
	private String parameterName;

	@Override
	protected void doFilter(HttpServletRequest request,
							HttpServletResponse response,
							FilterChain chain) throws IOException, ServletException {

		request = new PrincipalRequestWrapper(request);
		chain.doFilter(request, response);

		// TODO: 用户登录后的初始Token是否也能搞到这里
		var userId = request.getSession(true).getAttribute("UserId");
		if (userId != null && dynamicToken && !Misc.idempotent(request)) {
			changeCsrfCookie(request, response);
		}
	}

	private void changeCsrfCookie(HttpServletRequest request, HttpServletResponse response) {
		var cookie = new Cookie(cookieName, UUID.randomUUID().toString());
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookie.setMaxAge(request.getSession().getMaxInactiveInterval());
		response.addCookie(cookie);
	}

	private class PrincipalRequestWrapper extends HttpServletRequestWrapper {

		public PrincipalRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public Principal getUserPrincipal() {
			return globalDomain.enter(doGetPrincipal());
		}

		// system principal?
		private WebPrincipal doGetPrincipal() {
			return Optional
					.ofNullable(getSession())
					.map(session -> session.getAttribute("UserId"))
					.filter((__) -> checkCSRF())
					.map((id) -> new WebPrincipal((Integer) id))
					.orElse(WebPrincipal.ANONYMOUS);
		}

		private boolean checkCSRF() {
			if (cookieName == null) {
				return true;
			}
			var nullable = Optional.ofNullable(WebUtils.getCookie(this, cookieName)).map(Cookie::getValue);

			if (headerName != null) {
				nullable = nullable.filter(token -> token.equals(getHeader(headerName)));
			}
			if (parameterName != null) {
				nullable = nullable.filter(token -> token.equals(getParameter(parameterName)));
			}
			return nullable.isPresent();
		}
	}
}
