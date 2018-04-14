package net.kaciras.blog.infrastructure.text;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("ConstantConditions")
public final class DFCMatcher {

	/**
	 * null is valid for payload, so use an object to indicate the payload is not set
	 */
	private static final Object NULL = new Object();

	private final Map<Character, Node> firstChars = new HashMap<>();
	private final Set<Character> stops = new HashSet<>();

	/**
	 * add a word, and attach a payload
	 *
	 * @param word    the word will find from text.
	 * @param payload payload, can be null.
	 */
	public void addWord(String word, Object payload) {
		if (word.isEmpty()) {
			return;
		}
		Map<Character, Node> map = firstChars;
		Node node = null;

		/* build character tree */
		for (char ch : word.toCharArray()) {
			node = map.computeIfAbsent(ch, $ -> new Node());
			map = node.children;
		}

		/* set payload for end node */
		node.payload = payload;
	}

	public void addStopWord(Character character) {
		stops.add(character);
	}

	/**
	 * find all matched words and pass them to consumer method.
	 *
	 * @param text     text to match
	 * @param consumer method to process all matched.
	 */
	public void match(String text, TextSegmentConsumer consumer) {
		char[] chars = text.toCharArray();
		Node node;
		Node lastNode = null;

		for (int i = 0; i < chars.length; i++) {
			node = firstChars.get(chars[i]);
			if (node == null) {
				continue;
			}
			boolean skip = false;
			int j = i + 1;
			int position = 0;

			for (; j <= chars.length && node != null; j++) {
				if(!skip && node.payload != NULL) {
					position = j;
					lastNode = node;
				}
				if (j == chars.length) {
					break;
				}
				Character ch = chars[j];
				if (stops.contains(ch)) {
					skip = true;
				} else {
					skip = false;
					node = node.children.get(ch);
				}
			}
			if (position > i) {
				consumer.handle(i, position, lastNode.payload);
				i = position - 1;
			}
		}
	}

	public String replace(String text, Function<Object, String> replacer) {
		StringBuilder sb = new StringBuilder(); //The length of the result may differ from the original text
		IntHolder position = new IntHolder();

		match(text, (start, end, payload) -> {
			sb.append(text, position.value, start);
			position.value = end;
			sb.append(replacer.apply(payload));
		});
		sb.append(text, position.value, text.length());
		return sb.toString();
	}

	private static final class Node {

		private Object payload = DFCMatcher.NULL;
		private Map<Character, Node> children = new TreeMap<>();
	}

	/*
	 * match algorithm use one for-loop.
	 */
//	public void match_old(String text, TextSegmentConsumer consumer) {
//		char[] chars = text.toCharArray();
//
//		Node lastNode = null;
//		Map<Character, Node> map = firstChars;
//		int start = 0;
//		int position = 0;
//
//		for (int i = 0; i < chars.length || map != firstChars; ) {
//			Node node;
//			if (i == chars.length) {
//				node = null;
//				i++;
//			} else {
//				char ch = chars[i];
//				i++;
//				if (stops.contains(ch)) {
//					continue;
//				}
//				node = map.get(ch);
//			}
//			if (node == null) { //node==null is an ending of the tree
//				if (position > start) {
//					consumer.handle(start, position, lastNode.payload);
//					i = position;
//				} else if (map != firstChars) {
//					i = start + 1;
//				}
//				start = i;
//				map = firstChars;
//			} else {
//				if (node.payload != NULL) {
//					position = i;
//					lastNode = node;
//				}
//				map = node.children;
//			}
//		}
//
//		//don't forge the last segment.
//		if (position > start) {
//			consumer.handle(start, position, lastNode.payload);
//		}
//	}
}
