package net.kaciras.blog.infrastructure;

import net.kaciras.blog.infrastructure.exception.*;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

final class ExceptionResolverTest {

	private static final Collection<Class<? extends WebBusinessException>> EXCEPTIONS = List.of(
			DataTooBigException.class,
			LegallyProhibitedException.class,
			PermissionException.class,
			RequestArgumentException.class,
			RequestFrequencyException.class,
			ResourceDeletedException.class,
			ResourceNotFoundException.class,
			ResourceStateException.class
	);

	@SuppressWarnings("ConstantConditions")
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
