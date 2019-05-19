package net.kaciras.blog.infrastructure;

import net.kaciras.blog.infrastructure.principal.AuthorizationProperties;
import net.kaciras.blog.infrastructure.principal.ServletPrincipalFilter;
import net.kaciras.blog.infrastructure.principal.WebPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletPrincipalFilterTest {

	@FunctionalInterface
	private interface HttpFilterChain {
		void next(HttpServletRequest req, HttpServletResponse res);
	}

	private HttpServletResponse doFilter(Filter filter, HttpServletRequest request, HttpFilterChain chain) throws Exception {
		var response = new MockHttpServletResponse();
		filter.doFilter(request, response, (q, r) -> chain.next((HttpServletRequest) q, (HttpServletResponse) r));
		return response;
	}

	@Test
	void noCredit() throws Exception {
		var props = new AuthorizationProperties();
		var filter = new ServletPrincipalFilter(props, v -> v);

		doFilter(filter, new MockHttpServletRequest(), (req, res) ->
				Assertions.assertEquals(WebPrincipal.ANONYMOUS, req.getUserPrincipal()));
	}

	@Test
	void login() throws Exception {
		var props = new AuthorizationProperties();
		var filter = new ServletPrincipalFilter(props, v -> v);

		var session = new MockHttpSession();
		session.setAttribute("UserId", 666);

		var request = new MockHttpServletRequest();
		request.setSession(session);
		request.setCookies(new Cookie(props.getCsrfCookieName(), "FOOBAR"));
		request.addHeader(props.getCsrfHeaderName(), "FOOBAR");

		doFilter(filter, request, (req, res) -> {
			var principal = (WebPrincipal) req.getUserPrincipal();
			Assertions.assertEquals(666, principal.getId());
		});
	}

	@Test
	void invalidHeader() throws Exception {
		var props = new AuthorizationProperties();
		var filter = new ServletPrincipalFilter(props, v -> v);

		var session = new MockHttpSession();
		session.setAttribute("UserId", 666);

		var request = new MockHttpServletRequest();
		request.setSession(session);
		request.setCookies(new Cookie(props.getCsrfCookieName(), "FOOBAR"));
		request.addHeader(props.getCsrfHeaderName(), "invalid");

		doFilter(filter, request, (req, res) ->
				Assertions.assertEquals(WebPrincipal.ANONYMOUS, req.getUserPrincipal()));
	}

	@Test
	void changeToken() throws Exception {
		var props = new AuthorizationProperties();
		props.setDynamicCsrfCookie(true);
		var filter = new ServletPrincipalFilter(props, v -> v);

		var session = new MockHttpSession();
		session.setAttribute("UserId", 666);

		var request = new MockHttpServletRequest();
		request.setSession(session);
		request.setCookies(new Cookie(props.getCsrfCookieName(), "FOOBAR"));
		request.addHeader(props.getCsrfHeaderName(), "FOOBAR");

		var response = doFilter(filter, request, (req, res) -> {});
		Assertions.assertNotNull(response.getHeader("Set-Cookie"));
	}
}
