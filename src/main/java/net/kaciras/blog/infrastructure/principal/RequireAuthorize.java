package net.kaciras.blog.infrastructure.principal;

import net.kaciras.blog.infrastructure.exception.PermissionException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuthorize {

	String value() default "";

	Class<? extends Exception> error() default PermissionException.class;
}
