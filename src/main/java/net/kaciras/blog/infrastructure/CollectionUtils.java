package net.kaciras.blog.infrastructure;

public final class CollectionUtils {

	/**
	 * Helper method to get fitst element from a iterable.
	 *
	 * @param iterable iterable object.
	 * @param <T> type of element.
	 * @return the element.
	 * @throws IllegalArgumentException if iterable has no element.
	 */
	private static <T> T getFirst(Iterable<T> iterable) {
		var iter = iterable.iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		throw new IllegalArgumentException("iterable has no element.");
	}

	private CollectionUtils() {}
}
