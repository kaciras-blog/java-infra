package net.kaciras.blog.infrastructure.text;

import java.nio.CharBuffer;
import java.util.*;

/**
 * Detects the text contains dangerous word.
 * This class don't load any word set when instantiated, you should add words manually.
 * This class is not thread-safe.
 */
public final class DangerWordDetector {

	private DFCMatcher dfcMatcher = new DFCMatcher();

	public void addWords(String... word) {
		Arrays.stream(word).forEach(w -> dfcMatcher.addWord(w, null));
	}

	public void addStopChars(char... stop) {
		CharBuffer.wrap(stop).chars().mapToObj(ch -> (char) ch).forEach(dfcMatcher::addStopWord);
	}

	public List<String> getDangerWords(String text) {
		List<String> words = new ArrayList<>();
		dfcMatcher.match(text, (start, end, payload) -> words.add(text.substring(start, end)));
		return words;
	}

	public String replace(String text, String newWord) {
		return dfcMatcher.replace(text, payload -> newWord);
	}
}
