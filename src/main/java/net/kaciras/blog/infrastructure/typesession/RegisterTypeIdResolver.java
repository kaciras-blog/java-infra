package net.kaciras.blog.infrastructure.typesession;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import net.kaciras.blog.infrastructure.event.article.ArticleCreatedEvent;

import java.io.IOException;

public class RegisterTypeIdResolver extends TypeIdResolverBase {

	@Override
	public String idFromValue(Object value) {
		return idFromValueAndType(value, value.getClass());
	}

	@Override
	public String idFromValueAndType(Object value, Class<?> clazz) {
		return clazz.getSimpleName();
	}

	@Override
	public JavaType typeFromId(DatabindContext context, String id) throws IOException {
		if (id.equals("Inner")) {
			return context.constructType(ArticleCreatedEvent.Inner.class);
		}
		return context.constructType(ArticleCreatedEvent.class);
	}

	@Override
	public JsonTypeInfo.Id getMechanism() {
		return null;
	}
}
