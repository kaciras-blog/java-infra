package net.kaciras.blog.infrastructure.autoconfigure;

import net.kaciras.blog.infrastructure.principal.AuthorizeAspect;
import net.kaciras.blog.infrastructure.principal.ServletPrincipalFilter;
import net.kaciras.blog.infrastructure.principal.ServletSecurityContextFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

final class KxPrincipalAutoConfigurationTest {

	private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(KxPrincipalAutoConfiguration.class));

	// 没啥好测的，就测一下启动算了
	@Test
	void defaults () {
		contextRunner.run(context -> {
			assertThat(context).hasSingleBean(ServletPrincipalFilter.class);
			assertThat(context).doesNotHaveBean(ServletSecurityContextFilter.class);
			assertThat(context).hasSingleBean(AuthorizeAspect.class);
		});
	}

	@Test
	void enableSecurityContext() {
		contextRunner.withPropertyValues("kaciras.authorization.security-context=true")
				.run(context -> assertThat(context).hasSingleBean(ServletSecurityContextFilter.class));
	}
}
