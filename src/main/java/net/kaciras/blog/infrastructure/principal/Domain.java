package net.kaciras.blog.infrastructure.principal;

@FunctionalInterface
public interface Domain {

	WebPrincipal enter(WebPrincipal principal);
}
