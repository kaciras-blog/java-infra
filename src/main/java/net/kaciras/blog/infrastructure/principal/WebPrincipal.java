package net.kaciras.blog.infrastructure.principal;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.security.Principal;

@EqualsAndHashCode(of = "id")
@Value
public class WebPrincipal implements Principal {

	public static final int ANYNOMOUS_ID = 0;
	public static final int SYSTEM_ID = 1;
	public static final int ADMIN_ID = 2;

	private final int id;

	public boolean isLogined() {
		return id > 1;
	}

	public boolean isSystem() {
		return id == SYSTEM_ID;
	}

	public boolean isAnynomous() {
		return id == ANYNOMOUS_ID;
	}

	public boolean isAdministor() {
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
		return isAdministor() || isSystem();
	}

	@Override
	public String getName() {
		switch (id) {
			case 0:
				return "Anynomous";
			case 1:
				return "System";
		}
		return "Logined:" + id;
	}
}
