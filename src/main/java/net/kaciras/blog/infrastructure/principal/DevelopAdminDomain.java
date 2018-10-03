package net.kaciras.blog.infrastructure.principal;

import lombok.RequiredArgsConstructor;

/**
 * 开发管理员领域？没想到个好名字。
 * 开发测试用，将匿名用户转换为管理员，便于调试。
 */
@RequiredArgsConstructor
final class DevelopAdminDomain implements Domain {

	private final Domain innerDomain;

	@Override
	public WebPrincipal enter(WebPrincipal principal) {
		if (principal.isAnynomous()) {
			principal = new WebPrincipal(WebPrincipal.ADMIN_ID);
		}
		return innerDomain.enter(principal);
	}
}
