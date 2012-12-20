package immutable;

import immutable.TokenType;
import utilities.StringUtilities;

/**
 * This class is an information holder for a Token.
 * 
 * Note: This class does not contain any parsing logic. See TokenParser instead. 
 * 
 * @author Trironk Kiatkungwanglai
 */
public class Token {

	/**
	 * This value represents the TokenType of this particular Token.
	 */
	public final TokenType tokenType;
	
	/**
	 * This value is the string representation of this Token.
	 */
	public final String contents;
	
	/**
	 * Constructs a Token object.
	 */
	public Token(TokenType tokenType, String contents) {
		this.tokenType = tokenType;
		this.contents = contents;
	}
	
	/**
	 * Returns a String representation of this object.
	 */
	public String toString() {
		return '[' + StringUtilities.padCenter(tokenType.name, 15) + "]: " +
				StringUtilities.escaped(contents); 
	}
}
