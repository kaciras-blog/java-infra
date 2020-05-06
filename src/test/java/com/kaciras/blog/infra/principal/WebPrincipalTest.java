package com.kaciras.blog.infra.principal;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

final class WebPrincipalTest {

	@Test
	void getName() {
		var admin = new WebPrincipal(WebPrincipal.ADMIN_ID);
		var anonymous = new WebPrincipal(WebPrincipal.ANONYMOUS_ID);
		var u45 = new WebPrincipal(45);
		var u123 = new WebPrincipal(123);

		assertThat(admin.getName()).isEqualTo(new WebPrincipal(WebPrincipal.ADMIN_ID).getName());
		assertThat(anonymous.getName()).isEqualTo(WebPrincipal.ANONYMOUS.getName());
		assertThat(u45.getName()).isEqualTo(u45.getName());

		assertThat(Set.of(u123, u45, admin, anonymous)).size().isEqualTo(4);
	}

	@Test
	void system() {
		var principal = new WebPrincipal(WebPrincipal.SYSTEM_ID);

		assertThat(principal.getId()).isEqualTo(WebPrincipal.SYSTEM_ID);
		assertThat(principal.isSystem()).isTrue();
		assertThat(principal.isAdminister()).isFalse();
		assertThat(principal.getName()).isEqualTo("System");
		assertThat(principal.hasPermission("Any")).isTrue();
	}
}
