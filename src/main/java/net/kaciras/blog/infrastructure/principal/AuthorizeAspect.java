package net.kaciras.blog.infrastructure.principal;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 对注解 RequirePrincipal 的方法或类中的方法在调用前进行鉴权。
 * 权限比较简单的时候，直接用注解来过滤更方便，但权限比较复杂时还是
 * 需要些更详细的手动鉴权。
 */
@Slf4j
@Aspect
public final class AuthorizeAspect {

	@Pointcut("@within(net.kaciras.blog.infrastructure.principal.RequireAuthorize)")
	private void clazz() {}

	@Pointcut("@annotation(net.kaciras.blog.infrastructure.principal.RequireAuthorize)")
	private void method() {}

	/**
	 * 类上存在注解但方法上不存在时，以类上的注解来鉴权。
	 *
	 * @param joinPoint 切点
	 * @throws Exception 如果鉴权失败则抛出异常。
	 */
	@Before("clazz() && !method() && execution(!private * *(..))")
	public void beforeClass(JoinPoint joinPoint) throws Exception {
		var annotation = (RequireAuthorize) joinPoint.getSignature()
				.getDeclaringType()
				.getDeclaredAnnotation(RequireAuthorize.class);
		check(annotation, joinPoint);
	}

	/**
	 * 方法上存在注解，需要鉴权。
	 *
	 * @param joinPoint 切点
	 * @throws Exception 如果鉴权失败则抛出异常。
	 */
	@Before("method() && execution(!private * *(..))")
	public void beforeMethod(JoinPoint joinPoint) throws Exception {
		var annotation = ((MethodSignature) joinPoint.getSignature())
				.getMethod()
				.getDeclaredAnnotation(RequireAuthorize.class);
		check(annotation, joinPoint);
	}

	private void check(RequireAuthorize annotation, JoinPoint joinPoint) throws Exception {
		if (SecurityContext.getPrincipal().hasPermission(annotation.value())) {
			return;
		}
		logger.info("Permission check failed for method: " + joinPoint.getSignature());
		throw annotation.error().getConstructor().newInstance();
	}
}
