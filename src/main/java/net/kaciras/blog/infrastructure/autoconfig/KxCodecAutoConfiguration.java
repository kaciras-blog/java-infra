package net.kaciras.blog.infrastructure.autoconfig;

import net.kaciras.blog.infrastructure.codec.ExtendsCodecModule;
import net.kaciras.blog.infrastructure.codec.ImageRefrenceTypeHandler;
import net.kaciras.blog.infrastructure.codec.IpAddressTypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KxCodecAutoConfiguration {

	@ConditionalOnClass(ConfigurationCustomizer.class)
	@Configuration
	static class MybatisConfiguration {

		@Bean
		public ConfigurationCustomizer mybatisCustomizer() {
			return config -> {
				var registry = config.getTypeHandlerRegistry();
				registry.register(ImageRefrenceTypeHandler.class);
				registry.register(IpAddressTypeHandler.class);
			};
		}
	}

	@ConditionalOnClass(Jackson2ObjectMapperBuilderCustomizer.class)
	@Configuration
	static class JacksonConfiguration {

		@SuppressWarnings("unchecked")
		@Bean
		public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
			return builder -> builder.modulesToInstall(ExtendsCodecModule.class);
		}
	}
}
