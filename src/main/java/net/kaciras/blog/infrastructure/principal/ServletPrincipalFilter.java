package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
public final class ServletPrincipalFilter extends HttpFilter {

	private final AuthorizationProperties properties;

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new PrincipalRequestWrapper(request), response);
	}

	private class PrincipalRequestWrapper extends HttpServletRequestWrapper {

		public PrincipalRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public Principal getUserPrincipal() {
			var session = super.getSession();
			Object userId;

			if (properties.isDebugMode()) {
				return new WebPrincipal(WebPrincipal.ADMIN_ID);
			}
			if (session == null || (userId = session.getAttribute("UserId")) == null || !checkCSRF()) {
				return new WebPrincipal(WebPrincipal.ANYNOMOUS_ID);
			}
			// system principal?
			return new WebPrincipal((Integer) userId);
		}

		private boolean checkCSRF() {
			if (!properties.isCsrfVerify()) {
				return true; //在配置文件里可以关闭CSRF检验
			}
			var csrf = getSession().getAttribute(properties.getCsrfSessionName());
			return csrf != null && csrf.equals(getHeader(properties.getCsrfHeaderName()));
		}
	}
}
