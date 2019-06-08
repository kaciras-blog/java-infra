package net.kaciras.blog.infrastructure.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@ConfigurationProperties("kaciras.cors")
@Data
public final class CorsProperties {

	@Nullable
	private CorsTemplate template;

	private List<String> allowedOrigins;
	private List<String> allowedMethods;
	private List<String> allowedHeaders;

	private List<String> exposedHeaders;

	private Long maxAge;

	public enum CorsTemplate {

		/**
		 * 将CORS配置为Spring中的默认状态。
		 *
		 * @see CorsConfiguration#applyPermitDefaultValues()
		 */
		Default,

		/**
		 * 将CORS配置为允许所有（Origin，Allowed-Headers，Method...），这些属性都设为"*"
		 * 注意 Exposed-Headers 不支持通配而必须手动设置
		 */
		AllowAll
	}
}
