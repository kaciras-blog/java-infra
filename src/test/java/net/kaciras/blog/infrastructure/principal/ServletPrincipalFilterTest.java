package net.kaciras.blog.infrastructure.principal;

import net.kaciras.blog.infrastructure.FilterChainCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;

final class ServletPrincipalFilterTest {

	private static final String COOKIE_NAME = "CSRF-Token";
	private static final String HEADER_NAME = "X-CSRF-Token";
	private static final String PARAMETER_NAME = "csrf";

	private ServletPrincipalFilter filter;
	private HttpSession sessionUser666;

	@BeforeEach
	void setUp() {
		filter = new ServletPrincipalFilter(false);
		sessionUser666 = new MockHttpSession();
		sessionUser666.setAttribute("UserId", 666);
	}

	@Test
	void noCredit() throws Exception {
		var result = FilterChainCapture.doFilter(filter, new MockHttpServletRequest());
		assertThat(result.outRequest.getUserPrincipal()).isEqualTo(WebPrincipal.ANONYMOUS);
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
		assertThat(principal.getId()).isEqualTo(666);
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
		assertThat(result.outRequest.getUserPrincipal()).isEqualTo(WebPrincipal.ANONYMOUS);
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
		assertThat(principal.getId()).isEqualTo(666);
	}

	@Test
	void invalidParameter() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setParameterName(PARAMETER_NAME);

		var request = new MockHttpServletRequest();
		request.setSession(sessionUser666);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));

		var result = FilterChainCapture.doFilter(filter, request);
		assertThat(result.outRequest.getUserPrincipal()).isEqualTo(WebPrincipal.ANONYMOUS);
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
		assertThat(cookie.getName()).isEqualTo(COOKIE_NAME);
		assertThat(cookie.getDomain()).isEqualTo("kaciras.example.com");
	}

	@Test
	void skipSafeRequest() throws Exception {
		filter.setCookieName(COOKIE_NAME);
		filter.setHeaderName(HEADER_NAME);
		filter.setSkipSafeRequest(true);

		var request = new MockHttpServletRequest(null, "GET", "/");
		request.setSession(sessionUser666);
		request.setCookies(new Cookie(COOKIE_NAME, "FOOBAR"));

		var result = FilterChainCapture.doFilter(filter, request);
		var principal = (WebPrincipal) result.outRequest.getUserPrincipal();
		assertThat(principal.getId()).isEqualTo(666);

		request.setMethod("POST");
		result = FilterChainCapture.doFilter(filter, request);
		assertThat(result.outRequest.getUserPrincipal()).isEqualTo(WebPrincipal.ANONYMOUS);
	}
}
