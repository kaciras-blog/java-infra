package net.kaciras.blog.infrastructure;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;


class DisableHttpClientVerifyTest {

	private static DisposableServer server;

	@BeforeAll
	static void startServer() throws Exception {
		((Logger) LoggerFactory.getLogger("reactor")).setLevel(Level.ERROR);
		((Logger) LoggerFactory.getLogger("io.netty")).setLevel(Level.ERROR);

		var cert = new SelfSignedCertificate();
		var sslContextBuilder = SslContextBuilder.forServer(cert.certificate(), cert.privateKey());

		var adapter = new ReactorHttpHandlerAdapter((request, response) ->
				response.writeWith(Mono.just(response.bufferFactory().wrap("Hellow".getBytes())))
		);

		server = HttpServer.create()
				.protocol(HttpProtocol.HTTP11, HttpProtocol.H2)
				.secure(sslContextSpec -> sslContextSpec.sslContext(sslContextBuilder))
				.handle(adapter).bindNow();
	}

	@AfterAll
	static void closeServer() {
		server.disposeNow();
	}

	@Test
	void testHttpsURLConnectionFail() throws Exception {
		var url = new URL("https://localhost:" + server.port());
		Assertions.assertThatThrownBy(url::openStream).isInstanceOf(SSLHandshakeException.class);
	}

	@Test
	void testHttpsURLConnectionWithDisabled() throws Exception {
		var url = new URL("https://localhost:" + server.port());
		var conn = ((HttpsURLConnection) url.openConnection());

		var sslc = Misc.createTrustAllSSLContext();
		conn.setHostnameVerifier((host, session) -> true);
		conn.setSSLSocketFactory(sslc.getSocketFactory());

		try (var stream = conn.getInputStream()) {
			Assertions.assertThat(stream.readAllBytes()).containsExactly("Hellow".getBytes());
		}
	}

	@Test
	void testHttpClientFail() {
		var request = HttpRequest.newBuilder()
				.uri(URI.create("https://localhost:" + server.port()))
				.build();
		Assertions.assertThatThrownBy(() -> HttpClient.newHttpClient().send(request, BodyHandlers.ofString()))
				.isInstanceOf(IOException.class);
	}

	@Test
	void testHttpClientWithDisabled() throws Exception {
		var request = HttpRequest.newBuilder()
				.uri(URI.create("https://localhost:" + server.port()))
				.build();
		var res = HttpClient.newBuilder()
				.sslContext(Misc.createTrustAllSSLContext())
				.build()
				.send(request, BodyHandlers.ofString());
		Assertions.assertThat(res.body()).isEqualTo("Hellow");
	}
}