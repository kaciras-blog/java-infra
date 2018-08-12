package net.kaciras.blog.infrastructure.codec;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
public class KxCodecConfiguration {

	@ConditionalOnClass(ConfigurationCustomizer.class)
	@Bean
	public ConfigurationCustomizer mybatisCustomizer() {
		return config -> {
			var registry = config.getTypeHandlerRegistry();
			registry.register(ImageRefrenceTypeHandler.class);
			registry.register(IpAddressTypeHandler.class);
		};
	}

	@SuppressWarnings("unchecked")
	@ConditionalOnClass(Jackson2ObjectMapperBuilderCustomizer.class)
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
		return builder -> builder.modulesToInstall(ExtendsCodecModule.class);
	}
}
