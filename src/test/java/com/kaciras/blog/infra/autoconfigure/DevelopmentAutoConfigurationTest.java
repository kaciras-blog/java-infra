package com.kaciras.blog.infra.autoconfigure;

import com.kaciras.blog.infra.FilterChainCapture;
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
	void delayFilter() {
		contextRunner.withPropertyValues("kaciras.development.httpDelay=100ms").run(context -> {
			var delayFilter = context.getBean(Filter.class);

			// warm up
			FilterChainCapture.doFilter((req, res, chain) -> {});

			var begin = System.currentTimeMillis();
			var capture = FilterChainCapture.doFilter(delayFilter);
			var end = System.currentTimeMillis();

			assertThat(end - begin).isCloseTo(100, Offset.offset(30L));
			assertThat(capture.outRequest).isNotNull();
		});
	}
}
