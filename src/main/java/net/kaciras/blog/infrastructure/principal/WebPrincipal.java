package net.kaciras.blog.infrastructure.principal;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.security.Principal;
import java.util.Collection;

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

	public boolean hasPermission(String name) {
		return isAdministor() || isSystem();
	}

	public Collection<Role> getRoles() {
		throw new UnsupportedOperationException();
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
