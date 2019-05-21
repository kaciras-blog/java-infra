package net.kaciras.blog.infrastructure;

import net.kaciras.blog.infrastructure.principal.ServletPrincipalFilter;
import net.kaciras.blog.infrastructure.principal.WebPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

public class ServletPrincipalFilterTest {

	private static final String COOKIE_NAME = "CSRF-Token";
	private static final String HEADER_NAME = "X-CSRF-Token";
	private static final String PARAMETER_NAME = "csrf";

	private ServletPrincipalFilter filter;
	private HttpSession sessionUser666;

	@BeforeEach
	void setUp() {
		filter = new ServletPrincipalFilter(v -> v);
		sessionUser666 = new MockHttpSession();
		sessionUser666.setAttribute("UserId", 666);
	}

	@Test
	void noCredit() throws Exception {
		var result = FilterChainCapture.doFilter(filter, new MockHttpServletRequest());
		Assertions.assertEquals(WebPrincipal.ANONYMOUS, result.outRequest.getUserPrincipal());
	}

	@Test
	void validHeader() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setHeaderName(HEADER_NAME);

		var request = new MockHttpServletRequest();
		request.setSession(sessionUser666);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));
		request.addHeader(HEADER_NAME, "FOOBAR");

		var result = FilterChainCapture.doFilter(filter, request);

		var principal = (WebPrincipal) result.outRequest.getUserPrincipal();
		Assertions.assertEquals(666, principal.getId());
	}

	@Test
	void invalidHeader() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setHeaderName(HEADER_NAME);

		var request = new MockHttpServletRequest();
		request.setSession(sessionUser666);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));
		request.addHeader(HEADER_NAME, "invalid");

		var result = FilterChainCapture.doFilter(filter, request);
		Assertions.assertEquals(WebPrincipal.ANONYMOUS, result.outRequest.getUserPrincipal());
	}

	@Test
	void validParameter() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setParameterName(PARAMETER_NAME);

		var request = new MockHttpServletRequest();
		request.setSession(sessionUser666);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));
		request.addParameter(PARAMETER_NAME, "FOOBAR");

		var result = FilterChainCapture.doFilter(filter, request);

		var principal = (WebPrincipal) result.outRequest.getUserPrincipal();
		Assertions.assertEquals(666, principal.getId());
	}

	@Test
	void invalidParameter() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setParameterName(PARAMETER_NAME);

		var request = new MockHttpServletRequest();
		request.setSession(sessionUser666);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));

		var result = FilterChainCapture.doFilter(filter, request);
		Assertions.assertEquals(WebPrincipal.ANONYMOUS, result.outRequest.getUserPrincipal());
	}

	@Test
	void changeToken() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setDomain("kaciras.example.com");
		filter.setDynamicToken(true);

		var request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.setSession(sessionUser666);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));
		request.addHeader(HEADER_NAME, "FOOBAR");

		var result = FilterChainCapture.doFilter(filter, request);

		var cookie = MockCookie.parse(result.inResponse.getHeader("Set-Cookie"));
		Assertions.assertEquals(COOKIE_NAME, cookie.getName());
		Assertions.assertEquals("kaciras.example.com", cookie.getDomain());
	}
}
