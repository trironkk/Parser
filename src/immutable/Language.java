package immutable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import language.Alphabet;
import language._TokenType;
import language.defaults.RegExAlphabet;
import utilities.ConversionUtilities;
import utilities.StringUtilities;

/**
 * This is an information holder class, containing the TokenType objects.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class Language {

	/**
	 * This is the Alphabet for this particular table.
	 */
	public final Alphabet alphabet;

	/**
	 * The TokenTypes that define this Grammar.
	 * 
	 * Note: This instance variable is made to be immutable in the
	 * CreateDefinedLanguage method.
	 */
	public final Map<String, TokenType> tokenTypes;

	/**
	 * Standard constructor.
	 */
	public Language(Alphabet alphabet, Map<String, _TokenType> tokenTypes) {
		if (alphabet == null) {
			this.alphabet = RegExAlphabet.DEFAULT_ALPHABET;
		} else {
			this.alphabet = alphabet;
		}
		this.tokenTypes = lockTokenTypes(tokenTypes);
	}

	/**
	 * Converts a mapping of String objects to _TokenType objects to an
	 * immutable mapping of String objects to TokenType objects.
	 */
	private Map<String, TokenType> lockTokenTypes(
			Map<String, _TokenType> tokenTypes) {
		// Initialize the result
		Map<String, TokenType> result = new HashMap<String, TokenType>();

		// Iterate over all items in the given map
		for (Entry<String, _TokenType> entry : tokenTypes.entrySet()) {
			String name = entry.getKey();
			_TokenType oldTokenType = entry.getValue();

			// Convert the state machine to a DFA
			oldTokenType.stateMachine.convertToDFA();
			
			// Get a converted state machine
			StateMachine stateMachine = ConversionUtilities.convertStateMachine(
					oldTokenType.stateMachine);
			
			// Get the reserved word status
			boolean reservedWord = oldTokenType.reservedWord;
			
			// Aggregate all this information into a TokenType object
			TokenType tokenType = new TokenType(this, name, stateMachine,
					reservedWord);
			
			// Save the results
			result.put(name, tokenType);
		}
		return result;
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
