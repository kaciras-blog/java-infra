package net.kaciras.blog.infrastructure.exception;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Configuration
public class ExceptionResloverAutoConfiguration {

	@Bean
	public ExceptionReslover exceptionReslover() {
		return new ExceptionReslover();
	}

	@ResponseBody
	@ControllerAdvice
	private static final class ExceptionReslover {

		@ExceptionHandler
		public ResponseEntity handle(Exception ex) throws Exception {
			/* 自己定义的异常 */
			if (ex instanceof WebBusinessException) {
				return ResponseEntity
						.status(((WebBusinessException) ex).statusCode())
						.body(Map.of("message", ex.getMessage()));
			}

			/* 控制器方法的参数@Valid校验失败 */
			if (ex instanceof MethodArgumentNotValidException) {
				return ResponseEntity.status(400).body(Map.of("message", "请求参数或内容不合法"));
			}

			/* 控制器参数中对象的字段@Valid校验失败 */
			if (ex instanceof BindException) {
				return ResponseEntity.status(400).body(Map.of("message", "请求参数或内容不合法"));
			}

			throw ex; // 内部错误，或是MVC的异常就按默认处理
		}
	}
}
