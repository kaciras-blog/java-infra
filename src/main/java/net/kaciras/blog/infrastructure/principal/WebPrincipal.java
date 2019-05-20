package net.kaciras.blog.infrastructure.principal;

import lombok.Value;

import java.security.Principal;

@Value
public class WebPrincipal implements Principal {

	public static final int ANONYMOUS_ID = 0;
	public static final int SYSTEM_ID = 1;
	public static final int ADMIN_ID = 2;

	public static final WebPrincipal ANONYMOUS = new WebPrincipal(ANONYMOUS_ID);

	private final int id;

	public boolean isLogged() {
		return id > 1;
	}

	public boolean isSystem() {
		return id == SYSTEM_ID;
	}

	public boolean isAnonymous() {
		return id == ANONYMOUS_ID;
	}

	public boolean isAdminister() {
		return id == ADMIN_ID;
	}

	/**
	 * 判断该用户是否具有给定的权限。
	 * 默认的实现仅允许系统用户和管理员具有实现。
	 * 在进入Domain后，装饰的新身份上重写此方法来实现自定义权限逻辑。
	 *
	 * @param name 权限名
	 * @return 如果有则为true，反之false。
	 */
	public boolean hasPermission(String name) {
		return isAdminister() || isSystem();
	}

	public WebPrincipal exitDomain() {
		return this;
	}

	@Override
	public String getName() {
		switch (id) {
			case 0:
				return "Anonymous";
			case 1:
				return "System";
		}
		return "Logged:" + id;
	}
}
