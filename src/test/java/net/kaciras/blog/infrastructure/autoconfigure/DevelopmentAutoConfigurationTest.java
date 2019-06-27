package net.kaciras.blog.infrastructure.autoconfigure;

import net.kaciras.blog.infrastructure.FilterChainCapture;
import net.kaciras.blog.infrastructure.principal.Domain;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import javax.servlet.Filter;

import static org.assertj.core.api.Assertions.assertThat;

final class DevelopmentAutoConfigurationTest {

	private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(DevelopmentAutoConfiguration.class));

	@Test
	void defaults() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean(Domain.class);
			assertThat(context).doesNotHaveBean(Filter.class);
		});
	}

	@Test
	void delayFilter() {
		contextRunner.withPropertyValues("kaciras.development.httpDelay=100ms").run(context -> {
			var delayFilter = context.getBean(Filter.class);
			var begin = System.currentTimeMillis();

			var capture = FilterChainCapture.doFilter(delayFilter);

			// 精度这么差？
			assertThat(System.currentTimeMillis() - begin).isCloseTo(100, Offset.offset(50L));
			assertThat(capture.outRequest).isNotNull();
		});
	}
}