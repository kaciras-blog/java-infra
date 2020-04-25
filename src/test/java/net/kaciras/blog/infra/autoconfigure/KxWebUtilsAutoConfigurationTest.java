package net.kaciras.blog.infra.autoconfigure;

import net.kaciras.blog.infra.ExceptionResolver;
import net.kaciras.blog.infra.func.UncheckedRunnable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 本测试会启动真实的Tomcat服务器而不是Mock
final class KxWebUtilsAutoConfigurationTest {

	private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(KxWebUtilsAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class));

	private static final class TestServlet extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			resp.setStatus(200);
			resp.getWriter().write("Hello");
			resp.flushBuffer();
		}
	}

	private void runWithServer(WebApplicationContextRunner runner, UncheckedRunnable test) {
		runner.run(context -> {
			assertThat(context).hasBean("httpPortCustomizer");

			var factory = context.getBean(TomcatServletWebServerFactory.class);
			var server = (TomcatWebServer) factory.getWebServer(ctx ->
					ctx.addServlet("test", new TestServlet()).addMapping("/"));

			server.getTomcat().setSilent(true);
			server.start();
			try {
				test.runThrows();
			} finally {
				server.stop();
			}
		});
	}

	@Test
	void defaults() {
		contextRunner.run(context -> {
			assertThat(context).doesNotHaveBean("httpPortCustomizer");
			assertThat(context).hasSingleBean(ExceptionResolver.class);
		});
	}

	@Test
	void tomcatHttp11() {
		var runner = contextRunner.withPropertyValues("server.additional-connector.port=54321");
		runWithServer(runner, () -> {
			var request = HttpRequest.newBuilder(URI.create("http://localhost:54321")).build();
			var resp = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

			assertThat(resp.body()).isEqualTo("Hello");
			assertThat(resp.version()).isEqualTo(HttpClient.Version.HTTP_1_1);
		});
	}

	@Test
	void tomcatHttp2() {
		var runner = contextRunner.withPropertyValues(
				"server.additional-connector.port=54321",
				"server.http2.enabled=true"
		);
		runWithServer(runner, () -> {
			var request = HttpRequest.newBuilder(URI.create("http://localhost:54321")).build();
			var resp = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

			assertThat(resp.body()).isEqualTo("Hello");
			assertThat(resp.version()).isEqualTo(HttpClient.Version.HTTP_2);
		});
	}

	@Test
	void bindAddress() {
		var runner = contextRunner.withPropertyValues(
				"server.additional-connector.port=54321",
				"server.additional-connector.address=localhost"
		);
		runWithServer(runner, () -> {
			var request = HttpRequest.newBuilder(URI.create("http://localhost:54321")).build();
			var client = HttpClient.newHttpClient();

			assertThatThrownBy(() -> client.send(request, HttpResponse.BodyHandlers.ofString()))
					.isInstanceOf(ConnectException.class);
		});
	}
}
