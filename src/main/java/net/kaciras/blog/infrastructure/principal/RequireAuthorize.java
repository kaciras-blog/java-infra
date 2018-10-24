package net.kaciras.blog.infrastructure.principal;

import net.kaciras.blog.infrastructure.exception.PermissionException;

import java.lang.annotation.*;

/**
 * 注解在方法上时，该方法在调用前将检查 SecurityContext 中的用户是否具有
 * value() 所指定的权限，如果没有将抛出 error() 所指定的异常。注解在类上
 * 时，相当于注解在此类的所有方法上，但如果方法上也存在该注解，将以方法上的为准。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAuthorize {

	/**
	 * 执行方法所需的权限。
	 *
	 * @return 权限名
	 */
	String value() default "";

	/**
	 * 权限检查失败后抛出的异常。
	 *
	 * @return 异常类型
	 */
	Class<? extends Exception> error() default PermissionException.class;
}
