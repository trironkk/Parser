package language;

import java.util.Iterator;
import java.util.Map;

import language.defaults.RegExAlphabet;
import utilities.ErrorUtilities;
import utilities.StringUtilities;

/**
 * This is an information holder class, containing the TokenType objects.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class _Language {
	
	/**
	 * This is the Alphabet for this particular table.
	 */
	public Alphabet alphabet;
	
	/**
	 * The TokenTypes that define this Grammar.
	 */
	public Map<String, _TokenType> tokenTypes;
	
	/**
	 * Inserts a token type into the tokenTypes map.
	 */
	public void put(_TokenType tokenType) {
		if (tokenTypes.containsKey(tokenType.name)) {
			String msg = "Already contains a tokenType called " +
					tokenType.name + ".";
			ErrorUtilities.throwError(msg);
		}
		tokenTypes.put(tokenType.name, tokenType);
	}
	
	/**
	 * Constructs an instance of this class. This constructor should only be
	 * called by LanguageParser, and should not contain any parsing or
	 * organizational logic.
	 */
	public _Language(Alphabet alphabet, Map<String, _TokenType> tokenTypes) {
		if (alphabet == null)
			this.alphabet = RegExAlphabet.DEFAULT_ALPHABET;
		else
			this.alphabet = alphabet;
		
		this.tokenTypes = tokenTypes;
	}
	
	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		String result = new String();
		
		Iterator<String> iterator = tokenTypes.keySet().iterator();
		while (iterator.hasNext()) {
			result += tokenTypes.get(iterator.next()) + "\n";
		}

		result = "Language:\n" + StringUtilities.tabify(result);
		
		return result;
	}
}
