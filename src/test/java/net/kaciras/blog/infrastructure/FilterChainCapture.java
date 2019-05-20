package net.kaciras.blog.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public final class FilterChainCapture {

	public HttpServletRequest inRequest;
	public HttpServletResponse inResponse;

	public HttpServletRequest outRequest;
	public HttpServletResponse outResponse;

	public static FilterChainCapture doFilter(Filter filter, HttpServletRequest request) throws Exception {
		var capture = new FilterChainCapture();
		capture.inRequest = request;
		capture.inResponse = new MockHttpServletResponse();

		filter.doFilter(request, capture.inResponse, (q, r) -> {
			capture.outRequest = (HttpServletRequest) q;
			capture.outResponse = (HttpServletResponse) r;
		});
		return capture;
	}
}
