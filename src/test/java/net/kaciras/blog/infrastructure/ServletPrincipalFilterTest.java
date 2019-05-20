package net.kaciras.blog.infrastructure;

import net.kaciras.blog.infrastructure.principal.ServletPrincipalFilter;
import net.kaciras.blog.infrastructure.principal.WebPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletPrincipalFilterTest {

	private static final String COOKIE_NAME = "CSRF-Token";
	private static final String HEADER_NAME = "X-CSRF-Token";
	private static final String PARAMETER_NAME = "csrf";

	@FunctionalInterface
	private interface HttpFilterChain {
		void next(HttpServletRequest req, HttpServletResponse res);
	}

	private ServletPrincipalFilter filter = new ServletPrincipalFilter(v -> v);

	private HttpServletResponse doFilter(HttpServletRequest request, HttpFilterChain chain) throws Exception {
		var response = new MockHttpServletResponse();
		filter.doFilter(request, response, (q, r) -> chain.next((HttpServletRequest) q, (HttpServletResponse) r));
		return response;
	}

	@Test
	void noCredit() throws Exception {
		doFilter(new MockHttpServletRequest(), (req, res) ->
				Assertions.assertEquals(WebPrincipal.ANONYMOUS, req.getUserPrincipal()));
	}

	@Test
	void validHeader() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setHeaderName(HEADER_NAME);

		var session = new MockHttpSession();
		session.setAttribute("UserId", 666);

		var request = new MockHttpServletRequest();
		request.setSession(session);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));
		request.addHeader(HEADER_NAME, "FOOBAR");

		doFilter(request, (req, res) -> {
			var principal = (WebPrincipal) req.getUserPrincipal();
			Assertions.assertEquals(666, principal.getId());
		});
	}

	@Test
	void invalidHeader() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setHeaderName(HEADER_NAME);

		var session = new MockHttpSession();
		session.setAttribute("UserId", 666);

		var request = new MockHttpServletRequest();
		request.setSession(session);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));
		request.addHeader(HEADER_NAME, "invalid");

		doFilter(request, (req, res) ->
				Assertions.assertEquals(WebPrincipal.ANONYMOUS, req.getUserPrincipal()));
	}

	@Test
	void validParameter() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setParameterName(PARAMETER_NAME);

		var session = new MockHttpSession();
		session.setAttribute("UserId", 666);

		var request = new MockHttpServletRequest();
		request.setSession(session);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));
		request.addParameter(PARAMETER_NAME, "FOOBAR");

		doFilter(request, (req, res) -> {
			var principal = (WebPrincipal) req.getUserPrincipal();
			Assertions.assertEquals(666, principal.getId());
		});
	}

	@Test
	void invalidParameter() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setParameterName(PARAMETER_NAME);

		var session = new MockHttpSession();
		session.setAttribute("UserId", 666);

		var request = new MockHttpServletRequest();
		request.setSession(session);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));

		doFilter(request, (req, res) ->
				Assertions.assertEquals(WebPrincipal.ANONYMOUS, req.getUserPrincipal()));
	}

//	@Test
//	void changeToken() throws Exception {
//		props.setDynamicCsrfCookie(true);
//
//		var session = new MockHttpSession();
//		session.setAttribute("UserId", 666);
//
//		var request = new MockHttpServletRequest();
//		request.setSession(session);
//		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));
//		request.addHeader(HEADER_NAME, "FOOBAR");
//
//		var response = doFilter(request, (req, res) -> {});
//		Assertions.assertNotNull(response.getHeader("Set-Cookie"));
//	}
}
