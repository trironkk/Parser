package language;

import immutable.Language;
import immutable.TokenType;

import java.util.ArrayList;
import java.util.List;
import immutable.Token;
import utilities.StringUtilities;

/**
 * This class is an information holder.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class _TokenType
{
	/**
	 * A reference to the language to which this TokenType belongs.
	 */
	public Language language;
	
	/**
	 * This string serves as a reader-friendly name, extracted from the
	 * raw specification.
	 */
	public String name;
	
	/**
	 * This state machine matches all strings that match this token type.
	 */
	public _StateMachine stateMachine;
	
	/**
	 * This list contains all tokens involved in the definition of this
	 * _TokenType that have not been included in the state machine.
	 */
	public List<Token> unprocessedTokens;
	
	/**
	 * This list contains all tokens involved in the definition of this
	 * _TokenType that have been included in the state machine.
	 */
	public List<Token> processedTokens;
	
	/**
	 * This boolean signifies if a definition is for a token or not. If this
	 * value is true, the generated language will include this token type.
	 * Otherwise, it will not.
	 */
	public boolean tokenDefinition;
	
	/**
	 * True if this TokenType was specified with only RE_CHAR characters, and
	 * false otherwise. 
	 */
	public boolean reservedWord;
	
	/**
	 * Blank constructor.
	 */
	public _TokenType(String name) {
		this.name = name;
		this.unprocessedTokens = new ArrayList<Token>();
		this.processedTokens = new ArrayList<Token>();
		this.stateMachine = new _StateMachine();
		this.reservedWord = true;
	}

	/**
	 * This method removes one Token from unprocessedTokens, pushes it into
	 * processedToken, and returns it, or returns null if there are no more
	 * unprocessed tokens left.
	 */
	public Token popToken() {
		if (this.unprocessedTokens.isEmpty()) {
			return null;
		}
		this.processedTokens.add(this.unprocessedTokens.get(0));
		Token result = this.unprocessedTokens.get(0);
		this.unprocessedTokens.remove(0);
		return result;
	}
	
	/**
	 * This method returns the token type of the next token, or null if there
	 * are no more unprocessed tokens.
	 */
	public TokenType frontTokenType() {
		if (this.unprocessedTokens.isEmpty()) {
			return null;
		}
		return this.unprocessedTokens.get(0).tokenType;
	}
	
	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		return name + '\n' + StringUtilities.tabify(stateMachine.toString());
	}
}
