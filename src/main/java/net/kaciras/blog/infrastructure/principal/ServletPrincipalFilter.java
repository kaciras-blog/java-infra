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
@Order(1024)
public final class ServletPrincipalFilter extends HttpFilter {

	private final AuthorizationProperties properties;
	private final Domain globalDomain;

	@Override
	protected void doFilter(HttpServletRequest request,
							HttpServletResponse response,
							FilterChain chain)
			throws IOException, ServletException {

		request = new PrincipalRequestWrapper(request);
		chain.doFilter(request, response);

		if (properties.isDynamicCsrfCookie()
				&& ((WebPrincipal) request.getUserPrincipal()).isLogged()) {
			changeCsrfCookie(request, response);
		}
	}

	private void changeCsrfCookie(HttpServletRequest request, HttpServletResponse response) {
		var OldCookie = WebUtils.getCookie(request, properties.getCsrfSessionName());
		assert OldCookie != null;

		var newCookie = (Cookie) OldCookie.clone();
		newCookie.setValue(UUID.randomUUID().toString());
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
			var userId = Optional.ofNullable(getSession()).map(session -> session.getAttribute("UserId"));
			if (userId.isPresent() && checkCSRF()) {
				return new WebPrincipal((Integer) userId.get());
			}
			return new WebPrincipal(WebPrincipal.ANONYMOUS_ID);
		}

		private boolean checkCSRF() {
			if (!properties.isCsrfVerify()) {
				return true; // 在配置文件里可以关闭CSRF检验
			}
			var header = getHeader(properties.getCsrfHeaderName());
			var cookie = WebUtils.getCookie(this, properties.getCsrfSessionName());

			return Optional.ofNullable(cookie)
					.map(_cookie -> _cookie.getValue().equals(header))
					.orElse(false);
		}
	}
}
