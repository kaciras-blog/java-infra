package net.kaciras.blog.infrastructure.autoconfig;

import net.kaciras.blog.infrastructure.codec.ExtendsCodecModule;
import net.kaciras.blog.infrastructure.codec.ImageRefrenceTypeHandler;
import net.kaciras.blog.infrastructure.codec.IpAddressTypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动注册与 net.kaciras.blog.infrastructure.codec 包下的类相关的基础设施，
 * 包括Jackson的序列号模块、mybatis的TypeHandler。
 */
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
