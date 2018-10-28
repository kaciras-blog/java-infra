package net.kaciras.blog.infrastructure.principal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ServletSecurityContextFilter extends HttpFilter {

	@Override
	protected void doFilter(HttpServletRequest request,
							HttpServletResponse response,
							FilterChain chain)
			throws IOException, ServletException {

		SecurityContext.setPrincipal((WebPrincipal) request.getUserPrincipal());
		chain.doFilter(request, response);
		SecurityContext.setPrincipal(null); // protection.
	}
}
