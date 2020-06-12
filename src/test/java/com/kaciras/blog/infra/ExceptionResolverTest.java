package com.kaciras.blog.infra;

import com.kaciras.blog.infra.exception.WebBusinessException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.kaciras.blog.infra.TestHelper.getSubClassesInPackage;
import static org.assertj.core.api.Assertions.assertThat;

final class ExceptionResolverTest {

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends WebBusinessException>> EXCEPTIONS =
			getSubClassesInPackage(WebBusinessException.class, "com.kaciras.blog.infra.exception");

	@SuppressWarnings({"ConstantConditions", "rawtypes"})
	@Test
	void handleNotDebug() throws Exception {
		var resolver = new ExceptionResolver(false);

		for (var clazz : EXCEPTIONS) {
			var exception = clazz.getConstructor().newInstance();
			var response = resolver.handle(exception);
			var body = (Map) response.getBody();

			assertThat(response.getStatusCode().value()).isEqualTo(exception.statusCode());
			assertThat(body.get("message")).isEqualTo(exception.getMessage());
		}
	}
}
