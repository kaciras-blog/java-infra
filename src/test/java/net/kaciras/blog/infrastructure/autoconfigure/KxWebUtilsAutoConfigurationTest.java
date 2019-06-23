package net.kaciras.blog.infrastructure.autoconfigure;

import net.kaciras.blog.infrastructure.exception.ExceptionResolver;
import org.apache.catalina.connector.Connector;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

final class KxWebUtilsAutoConfigurationTest {

	private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(KxWebUtilsAutoConfiguration.class));

	@Test
	void defaults() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean(WebServerFactoryCustomizer.class);
			assertThat(context).hasSingleBean(ExceptionResolver.class);
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	void tomcatHttp() {
		var factory = mock(TomcatServletWebServerFactory.class);

		contextRunner.withPropertyValues("server.http-port=54321").run(context -> {
			var customizer = (WebServerFactoryCustomizer) context.getBean("addedHttpPort");
			customizer.customize(factory);

			var captor = ArgumentCaptor.forClass(Connector.class);
			verify(factory).addAdditionalTomcatConnectors(captor.capture());

			assertThat(captor.getValue().getPort()).isEqualTo(54321);
		});
	}
}
