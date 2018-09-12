package net.kaciras.blog.infrastructure;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使Http服务器支持双端口连接，例如同时监听80和443，额外的端口由选项server.http-port指定。
 */
@ConditionalOnProperty(name = "server.http-port")
@Configuration
public class AddontionPortAutoConfiguration {

	@Value("${server.http-port}")
	private int port;

	@ConditionalOnClass(TomcatServletWebServerFactory.class)
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer() {
		return factory -> {
			var connector = new Connector();
			connector.setPort(port);
			factory.addAdditionalTomcatConnectors(connector);
		};
	}

}
