package net.kaciras.blog.infrastructure.autoconfig;

import lombok.RequiredArgsConstructor;
import net.kaciras.blog.infrastructure.exception.WebBusinessException;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Configuration
public class KxWebUtilsAutoConfiguration {

	private final ServerProperties serverProperties;

	/**
	 * 使Http服务器支持双端口连接，例如同时监听80和443，额外的端口由选项server.http-port指定。
	 * 这回导致多一个Connector，消耗更多的资源。
	 *
	 * @since 1.6
	 * @param port HTTP连接端口
	 * @return 配置器
	 */
	@ConditionalOnProperty(name = "server.http-port")
	@ConditionalOnClass(TomcatServletWebServerFactory.class)
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer(@Value("${server.http-port}") int port) {
		return factory -> {
			var connector = new Connector();
			connector.setPort(port);
			((AbstractProtocol) connector.getProtocolHandler())
					.setMaxThreads(serverProperties.getTomcat().getMaxThreads());
			factory.addAdditionalTomcatConnectors(connector);
		};
	}

	@Bean
	public ExceptionResolver exceptionResolver() {
		return new ExceptionResolver();
	}

	@ControllerAdvice
	@ResponseBody
	private static final class ExceptionResolver {

		private static final Set<Class<?>> ARGUMENT_EXCEPTIONS = Set.of(
				MethodArgumentNotValidException.class,
				BindException.class,
				MethodArgumentTypeMismatchException.class
		);

		@ExceptionHandler
		public ResponseEntity handle(Exception ex) throws Exception {
			/* 自己定义的异常 */
			if (ex instanceof WebBusinessException) {
				return ResponseEntity
						.status(((WebBusinessException) ex).statusCode())
						.body(Map.of("message", ex.getMessage()));
			}

			/* 控制器方法的参数绑定失败 */
			if (ARGUMENT_EXCEPTIONS.contains(ex.getClass())) {
				return ResponseEntity.status(400).body(Map.of("message", "请求参数或内容不合法"));
			}

			throw ex; // 内部错误，或是MVC的异常就按默认处理
		}

		private String debugMessage(Exception e) {
			if(e instanceof MethodArgumentTypeMismatchException) {
				var ex = (MethodArgumentTypeMismatchException) e;
				return String.format("控制器：%s 的参数：%s 类型错误，预期：%s，实际：%s",
						ex.getParameter().getMethod(), ex.getName(), ex.getRequiredType(), ex.getValue());
			}
			if(e instanceof MethodArgumentNotValidException) {
				var ex = (MethodArgumentNotValidException) e;
				return ex.getMessage();
			}
			return "请求参数或内容不合法";
		}
	}
}
