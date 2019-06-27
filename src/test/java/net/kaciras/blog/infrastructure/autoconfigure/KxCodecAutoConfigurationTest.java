package net.kaciras.blog.infrastructure.autoconfigure;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static net.kaciras.blog.infrastructure.TestHelper.getSubClassesInPackage;
import static org.assertj.core.api.Assertions.assertThat;

final class KxCodecAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(KxCodecAutoConfiguration.class));

	@SuppressWarnings("unchecked")
	@Test
	void defaults() throws Exception {
		var shouldRegistered = getSubClassesInPackage(TypeHandler.class, "net.kaciras.blog.infrastructure.codec");

		contextRunner.run(context -> {
			var customizer = context.getBean(ConfigurationCustomizer.class);

			var mybatisConfig = new Configuration();
			customizer.customize(mybatisConfig);

			var handlers = mybatisConfig.getTypeHandlerRegistry()
					.getTypeHandlers()
					.stream().map(Object::getClass);

			assertThat(handlers).containsAll(shouldRegistered);
			assertThat(context).hasBean("jacksonCustomizer");
		});
	}
}
