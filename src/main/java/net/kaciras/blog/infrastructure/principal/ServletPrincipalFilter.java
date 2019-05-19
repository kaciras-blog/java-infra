package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Order(10_000)
public final class ServletPrincipalFilter extends HttpFilter {

	private final AuthorizationProperties properties;
	private final Domain globalDomain;

	@Override
	protected void doFilter(HttpServletRequest request,
							HttpServletResponse response,
							FilterChain chain) throws IOException, ServletException {

		request = new PrincipalRequestWrapper(request);
		chain.doFilter(request, response);

		if (properties.isDynamicCsrfCookie()
				&& ((WebPrincipal) request.getUserPrincipal()).isLogged()) {
			changeCsrfCookie(request, response);
		}
	}

	private void changeCsrfCookie(HttpServletRequest request, HttpServletResponse response) {
		var OldCookie = WebUtils.getCookie(request, properties.getCsrfCookieName());
		assert OldCookie != null;

		var newCookie = (Cookie) OldCookie.clone();
		newCookie.setValue(UUID.randomUUID().toString());
		// TODO: set cookie properties

		response.addCookie(newCookie);
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
			var cookie = WebUtils.getCookie(this, properties.getCsrfCookieName());
			var nullable = Optional.ofNullable(cookie).map(Cookie::getValue);

			if (properties.getCsrfHeaderName() != null) {
				nullable = nullable.filter(token -> token.equals(getHeader(properties.getCsrfHeaderName())));
			}
			if(properties.getCsrfParameterName() != null) {
				nullable = nullable.filter(token -> token.equals(getParameter(properties.getCsrfParameterName())));
			}
			return nullable.isPresent();
		}
	}
}
