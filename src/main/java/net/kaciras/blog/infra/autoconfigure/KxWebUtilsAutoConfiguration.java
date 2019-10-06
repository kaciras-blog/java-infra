package net.kaciras.blog.infra.autoconfigure;

import lombok.RequiredArgsConstructor;
import net.kaciras.blog.infra.exception.ExceptionResolver;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
		DevelopmentProperties.class,
		ServerProperties.class,
		AdditionalConnectorProperties.class,
})
@RequiredArgsConstructor
@Configuration
public class KxWebUtilsAutoConfiguration {

	private final DevelopmentProperties developmentProperties;
	private final ServerProperties serverProperties;
	private final AdditionalConnectorProperties additionalConnectorProperties;

	/**
	 * 使Http服务器支持双端口连接，例如同时监听80和443，额外的端口由选项server.http-port指定。
	 * 但这会导致多一个Connector，消耗更多的资源。
	 * <p>
	 * 【注意】Firefox 不准备支持 h2c 所以没法在浏览器上用 HTTP/2
	 *
	 * @return 配置器
	 */
	@ConditionalOnClass(TomcatServletWebServerFactory.class)
	@ConditionalOnProperty(name = "server.additional-connector.port")
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpPortCustomizer() {
		return factory -> {
			var connector = new Connector();
			connector.setPort(additionalConnectorProperties.getPort());

			var protocolHandler = (AbstractProtocol) connector.getProtocolHandler();
			protocolHandler.setMaxThreads(serverProperties.getTomcat().getMaxThreads());
			protocolHandler.setAddress(additionalConnectorProperties.getAddress());

			if (serverProperties.getHttp2().isEnabled()) {
				connector.addUpgradeProtocol(new Http2Protocol());
			}
			factory.addAdditionalTomcatConnectors(connector);
		};
	}

	@Bean
	public ExceptionResolver exceptionResolver() {
		return new ExceptionResolver(developmentProperties.isDebugErrorMessage());
	}
}
