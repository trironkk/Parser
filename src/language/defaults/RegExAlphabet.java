package language.defaults;

import language.Alphabet;

/**
 * This class exposes a default alphabet for the RegExGrammar, RegExLanguage,
 * and other involved logic to leverage.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class RegExAlphabet {
	/**
	 * The default Alphabet is the standard for ASCII, which contains 128 unique
	 * characters.
	 */
	public static final Alphabet DEFAULT_ALPHABET = new Alphabet(128);
}
