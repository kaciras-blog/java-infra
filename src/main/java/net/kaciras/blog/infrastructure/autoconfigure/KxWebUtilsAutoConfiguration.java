package net.kaciras.blog.infrastructure.autoconfigure;

import lombok.RequiredArgsConstructor;
import net.kaciras.blog.infrastructure.exception.ExceptionResolver;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({DevelopmentProperties.class, ServerProperties.class})
@RequiredArgsConstructor
@Configuration
public class KxWebUtilsAutoConfiguration {

	private final ServerProperties serverProperties;
	private final DevelopmentProperties properties;

	/**
	 * 使Http服务器支持双端口连接，例如同时监听80和443，额外的端口由选项server.http-port指定。
	 * 但这会导致多一个Connector，消耗更多的资源。
	 *
	 * 【注意】Firefox 不准备支持 h2c 所以没法在浏览器上用 HTTP/2
	 *
	 * @param port HTTP连接端口
	 * @return 配置器
	 */
	@ConditionalOnProperty(name = "server.extra-http-port")
	@ConditionalOnClass(TomcatServletWebServerFactory.class)
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpPortCustomizer(
			@Value("${server.extra-http-port}") int port) {
		return factory -> {
			var connector = new Connector();
			connector.setPort(port);

			var protocolHandler = (AbstractProtocol) connector.getProtocolHandler();
			protocolHandler.setMaxThreads(serverProperties.getTomcat().getMaxThreads());

			if (serverProperties.getHttp2().isEnabled()) {
				connector.addUpgradeProtocol(new Http2Protocol());
			}
			factory.addAdditionalTomcatConnectors(connector);
		};
	}

	@Bean
	public ExceptionResolver exceptionResolver() {
		return new ExceptionResolver(properties.isDebugErrorMessage());
	}
}
