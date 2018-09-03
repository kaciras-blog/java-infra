package net.kaciras.blog.infrastructure.sql;

import net.kaciras.blog.infrastructure.exception.ResourceNotFoundException;

public final class DBUtils {

	/**
	 * 检查Update，Delete等SQL语句是否产生了影响（影响的行数 > 0），没产生影响视为未找到
	 *
	 * @param rows 影响行数
	 * @throws ResourceNotFoundException 如果没有影响任何行
	 */
	public static void checkEffective(int rows) {
		if (rows <= 0) throw new ResourceNotFoundException();
	}

	/**
	 * 检查对象是否为null，如果是则抛出ResourceNotFoundException异常。
	 *
	 * @param obj 对象
	 * @param <T> 对象类型
	 * @return 原样返回参数obj
	 */
	public static <T> T checkNotNullResource(T obj) {
		if (obj == null)
			throw new ResourceNotFoundException();
		return obj;
	}

	public static <T> T checkNotNullResource(T obj, String message) {
		if (obj == null)
			throw new ResourceNotFoundException(message);
		return obj;
	}

	private DBUtils() {}
}
