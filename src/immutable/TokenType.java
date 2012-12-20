package immutable;

/**
 * This class is an information holder.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class TokenType
{
	/**
	 * A reference to the language to which this TokenType belongs.
	 */
	public final Language language;
	
	/**
	 * This string serves as a reader-friendly name, extracted from the
	 * raw specification.
	 */
	public final String name;

	/**
	 * This state machine matches all strings that match this token type.
	 */
	public final StateMachine stateMachine;
	
	/**
	 * True if this TokenType was specified with only RE_CHAR characters, and
	 * false otherwise. 
	 */
	public final boolean reservedWord;
	
	/**
	 * Standard constructor.
	 */
	public TokenType(Language language, String name,
			StateMachine stateMachine, boolean reservedWord) {
		this.language = language;
		this.name = name;
		this.stateMachine = stateMachine;
		this.reservedWord = reservedWord;
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		return this.name;
//		return this.name + '\n' + StringUtilities.tabify(
//				this.stateMachine.toString());
	}
}