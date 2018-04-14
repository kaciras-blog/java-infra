package net.kaciras.blog.infrastructure.text;

@FunctionalInterface
public interface TextSegmentConsumer {

	/**
	 * handle matched segment.
	 *
	 * @param start   the start index of word in text.
	 * @param end     the end index of word in text (include stop words).
	 * @param payload attached object when the word added to matcher.
	 */
	void handle(int start, int end, Object payload);
}
