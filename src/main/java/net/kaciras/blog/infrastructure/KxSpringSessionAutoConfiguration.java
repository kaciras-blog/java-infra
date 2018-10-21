package net.kaciras.blog.infrastructure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.time.Duration;

@EnableConfigurationProperties(SessionCookieProperties.class)
@ConditionalOnClass(Session.class)
@Configuration
public class KxSpringSessionAutoConfiguration {

	@ConditionalOnWebApplication(type = Type.SERVLET)
	@ConditionalOnMissingBean(CookieSerializer.class)
	@Configuration
	static class ServletSessionConfiguration {

		@Bean
		public CookieSerializer cookieSerializer(SessionCookieProperties options) {
			var serializer = new DefaultCookieSerializer();
			serializer.setDomainName(options.getDomain());
			serializer.setSameSite(options.getSameSite());
			serializer.setCookieMaxAge(options.getMaxAge());
			serializer.setUseSecureCookie(options.isSecure());

			// SpringSession默认使用UUID.toString()，没必要Base64，而且保持跟Webflux的一致.
			serializer.setUseBase64Encoding(false);
			return serializer;
		}
	}

	@ConditionalOnWebApplication(type = Type.REACTIVE)
	@Configuration
	static class ReactiveSessionConfiguration {

		@ConditionalOnMissingBean(WebSessionIdResolver.class)
		@Bean
		public WebSessionIdResolver sessionIdResolver(SessionCookieProperties options) {
			var res = new CookieWebSessionIdResolver();
			res.addCookieInitializer(builder -> builder
					.domain(options.getDomain())
					.sameSite(options.getSameSite())
					.secure(options.isSecure()));
			res.setCookieMaxAge(Duration.ofSeconds(options.getMaxAge()));
			return res;
		}
	}
}
