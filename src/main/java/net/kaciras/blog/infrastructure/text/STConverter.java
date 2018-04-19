package net.kaciras.blog.infrastructure.text;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * convert simplified Chinese to traditional Chinese, or reserve.
 * This class is not thread-safe.
 */
@Slf4j
public final class STConverter {

	private DFCMatcher s2t = new DFCMatcher();
	private DFCMatcher t2s = new DFCMatcher();

	/**
	 * create instance and load default dictionaries.
	 *
	 * @throws IOException if load fail.
	 */
	public STConverter() throws IOException {
		loadDictionaries("zhc/STCharacters.txt");
		loadDictionaries("zhc/STPhrases.txt");
	}

	/**
	 * load a dictionary from classpath, the exception of file not found will be ignored. if words
	 * be loaded more than once, the later will overwrite the previous.
	 *
	 * @param name dictionary file name.
	 * @throws IOException if an IOException occurred.
	 */
	public void loadDictionaries(String name) throws IOException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
		if(stream == null) {
			return; //ignore file not found.
		}
		InputStreamReader insr = new InputStreamReader(stream, StandardCharsets.UTF_8);

		try(BufferedReader reader = new BufferedReader(insr)) {
			reader.lines().map(line -> line.split("\\s")).forEach(p -> addMapping(p[0], p[1]));
		} catch (NullPointerException e) {
			throw new IOException("invalid format."); //p[xxx] throws
		}
		log.debug("loaded dictionary: {}", name);
	}

	/**
	 * add a word mapping.
	 *
	 * @param simple simplified Chinese word
	 * @param traditional traditional Chinese word
	 */
	public void addMapping(String simple, String traditional) {
		s2t.addWord(simple, traditional);
		t2s.addWord(traditional, simple);
	}

	public boolean removeSimpleMapping(String simple) {
		return s2t.removeWord(simple);
	}

	public boolean removeTraditionalMapping(String traditional){
		return t2s.removeWord(traditional);
	}

	/**
	 * convert a traditional Chinese text to a simplified Chinese text.
	 *
	 * @param text simplified Chinese text
	 * @return traditional Chinese text
	 */
	public String toSimplified(String text) {
		return t2s.replace(text, Object::toString);
	}

	/**
	 * convert a simplified Chinese text to a traditional Chinese text.
	 *
	 * @param text simplified Chinese text
	 * @return traditional Chinese text
	 */
	public String toTraditional(String text) {
		return s2t.replace(text, Object::toString);
	}
}
