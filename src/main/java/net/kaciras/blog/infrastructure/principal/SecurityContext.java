package net.kaciras.blog.infrastructure.principal;

import lombok.experimental.UtilityClass;
import net.kaciras.blog.infrastructure.exception.PermissionException;
import org.springframework.lang.NonNull;

import java.util.Objects;

@UtilityClass
public final class SecurityContext {

	private static final ThreadLocal<WebPrincipal> threadLocal = new ThreadLocal<>();

	public static void setPrincipal(WebPrincipal principal) {
		threadLocal.set(principal);
	}

	@NonNull
	public static WebPrincipal getPrincipal() {
		return Objects.requireNonNull(threadLocal.get(), "你需要添加 SecurityContextFilter 后才能使用 SecurityContext");
	}

	public static void enter(Domain domain) {
		threadLocal.set(domain.enter(threadLocal.get()));
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
