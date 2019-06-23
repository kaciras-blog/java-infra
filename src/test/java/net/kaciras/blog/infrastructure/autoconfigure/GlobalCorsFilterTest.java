package net.kaciras.blog.infrastructure.autoconfigure;

import net.kaciras.blog.infrastructure.FilterChainCapture;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.Filter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class GlobalCorsFilterTest {

	private final CorsProperties config = new CorsProperties();

	private Filter createFilter() {
		return new KxGlobalCorsAutoConfiguration(config).corsFilter().getFilter();
	}

	@Test
	void nonCors() throws Exception {
		var request = new MockHttpServletRequest();
		var result = FilterChainCapture.doFilter(createFilter(), request);
		assertThat(result.outRequest).isSameAs(request);
	}

	@Test
	void simpleRequest() throws Exception {
		config.setTemplate(CorsProperties.CorsTemplate.AllowAll);

		var request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.addHeader("Origin", "https://example.com");

		var result = FilterChainCapture.doFilter(createFilter(), request);
		assertThat(result.outRequest).isSameAs(request);
		assertThat(result.inResponse.getHeader("Access-Control-Allow-Origin")).isEqualTo("https://example.com");
	}

	// https://www.w3.org/TR/cors/#resource-preflight-requests
	@Test
	void preFlight() throws Exception {
		config.setTemplate(CorsProperties.CorsTemplate.AllowAll);

		var request = new MockHttpServletRequest();
		request.setMethod("OPTIONS");
		request.addHeader("Origin", "https://example.com");
		request.addHeader("Access-Control-Request-Method", "POST");

		var result = FilterChainCapture.doFilter(createFilter(), request);
		assertThat(result.outRequest).isNull();

		var response = result.inResponse;
		assertThat(response.getHeader("Access-Control-Allow-Origin")).isEqualTo("https://example.com");
		assertThat(response.getHeader("Access-Control-Allow-Methods")).isEqualTo("POST");
	}

	@Test
	void invalidPreFlight() throws Exception {
		config.setTemplate(CorsProperties.CorsTemplate.AllowAll);
		config.setAllowedOrigins(List.of("https://example.com"));

		var request = new MockHttpServletRequest();
		request.setMethod("OPTIONS");
		request.addHeader("Origin", "https://invalid.com");
		request.addHeader("Access-Control-Request-Method", "POST");

		var result = FilterChainCapture.doFilter(createFilter(), request);
		assertThat(result.outRequest).isNull();
		assertThat(result.inResponse.getStatus()).isEqualTo(403);
	}
}
