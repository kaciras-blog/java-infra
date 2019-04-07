package net.kaciras.blog.infrastructure.principal;

import lombok.experimental.UtilityClass;
import net.kaciras.blog.infrastructure.exception.PermissionException;

@UtilityClass
public final class SecurityContext {

	private static final ThreadLocal<WebPrincipal> threadLogal = new ThreadLocal<>();

	static void setPrincipal(WebPrincipal principal) {
		threadLogal.set(principal);
	}

	public static WebPrincipal getPrincipal() {
		return threadLogal.get();
	}

	public static void enter(Domain domain) {
		threadLogal.set(domain.enter(threadLogal.get()));
	}

/* ==================================== Helper Methods ==================================== */

	public static int getUserId() {
		return getPrincipal().getId();
	}

	public static void require(String perm) {
		if (!getPrincipal().hasPermission(perm)) throw new PermissionException();
	}

	/**
	 * 检查当前的用户是否不是参数id所指定的用户。
	 * 因为一般当用户和所需用户不同时才需要额外处理，所以设计为反义方法。
	 *
	 * @param id 用户id
	 * @return 如果当前用户不存在，或用户id与参数指定的id不同则返回true，否则false
	 */
	public static boolean isNot(int id) {
		return getPrincipal().getId() != id;
	}

	public static void requireId(int id) {
		if (isNot(id)) throw new PermissionException();
	}

	public static void requireLogin() {
		if (getPrincipal().isAnonymous()) throw new PermissionException();
	}

	public static boolean checkSelf(int id, String perm) {
		var principal = getPrincipal();
		return principal.getId() == id || principal.hasPermission(perm);
	}

	public static void requireSelf(int id, String perm) {
		if (!checkSelf(id, perm)) throw new PermissionException();
	}
}
