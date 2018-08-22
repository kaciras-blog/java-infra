package net.kaciras.blog.infrastructure.io;

import net.kaciras.blog.infrastructure.exception.ResourceNotFoundException;

public final class DBUtils {

	/**
	 * 用于检查Update，Delete等SQL语句是否产生了影响，没产生影响视为未找到
	 *
	 * @param rows 影响行数
	 * @throws ResourceNotFoundException 如果没有影响任何行
	 */
	public static void checkEffective(int rows) {
		if (rows <= 0) throw new ResourceNotFoundException();
	}

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
