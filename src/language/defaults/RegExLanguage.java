package language.defaults;

import immutable.Language;
import immutable.TokenType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import language.*;

/**
 * This class represents a regular expression Language, to be used in the
 * parsing of the regular expression that defines the Language.
 * 
 * @author Trironk Kiatkungwanglai
 */
public final class RegExLanguage {
	
	/**
	 * The names of the RegEx TokenTypes.
	 */
	public static final String UNION_NAME = "$UNION";
	public static final String RE_CHAR_NAME = "$RE_CHAR";
	public static final String CLS_CHAR_NAME = "$CLS_CHAR";
	public static final String OPEN_BRACKET_NAME = "$OPEN_BRACKET";
	public static final String CLOSE_BRACKET_NAME = "$CLOSE_BRACKET";
	public static final String OPEN_PARENS_NAME = "$OPEN_PARENS";
	public static final String CLOSE_PARENS_NAME = "$CLOSE_PARENS";
	public static final String PLUS_NAME = "$PLUS";
	public static final String STAR_NAME = "$STAR";
	public static final String DOT_NAME = "$DOT";
	public static final String DASH_NAME = "$DASH";
	public static final String CARROT_NAME = "$CARROT";
	public static final String IN_NAME = "$IN";
	public static final String DEFINED_CLASS_NAME = "$DEFINED_CLASS";
	public static final String EPSILON_NAME = "$EPSILON";
	public static final String NEW_LINE_NAME = "$NEW_LINE";
	
	public static final TokenType UNION;
	public static final TokenType RE_CHAR;
	public static final TokenType CLS_CHAR;
	public static final TokenType OPEN_BRACKET;
	public static final TokenType CLOSE_BRACKET;
	public static final TokenType OPEN_PARENS;
	public static final TokenType CLOSE_PARENS;
	public static final TokenType PLUS;
	public static final TokenType STAR;
	public static final TokenType DOT;
	public static final TokenType DASH;
	public static final TokenType CARROT;
	public static final TokenType IN;
	public static final TokenType DEFINED_CLASS;
	public static final TokenType EPSILON;
	public static final TokenType NEW_LINE;
	
	/**
	 * RE_CHAR escaped characters
	 */
	public static final char[] RE_CHAR_ESCAPE_CHARACTERS = new char[] { ' ',
			'\\', '*', '+', '?', '|', '[', ']', '(', ')', '.', '\'', '"', '\n',
			'$', '\t'};

	/**
	 * CLS_CHAR escaped characters
	 */
	public static final char[] CLS_CHAR_ESCAPE_CHARACTERS = new char[] { '\\',
			'^', '-', '[', ']', '\n', ' ' };
	
	/**
	 * DEFINED_CLASS invalid characters
	 */
	public static final char[] DEFINED_CLASS_INVALID_CHARACTERS = new char[] {
		'$', ' ', '|', '\n', '\t', '(', '[', ')', ']' };

	/**
	 * This is the only instance of this language, and it is made available
	 * with the singleton pattern.
	 */
	public static final Language instance;
	
	/**
	 * Static constructor initializing all the token types.
	 */
	static
	{
		_Language temporary = new _Language(null,
				new HashMap<String, _TokenType>());
		
		temporary.put(createUNIONTokenType());
		temporary.put(createRECHARTokenType());
		temporary.put(createCLSCHARTokenType());
		temporary.put(createOPENBRACKETTokenType());
		temporary.put(createCLOSEBRACKETTokenType());
		temporary.put(createOPENPARENSTokenType());
		temporary.put(createCLOSEPARENSTokenType());
		temporary.put(createPLUSTokenType());
		temporary.put(createSTARTokenType());
		temporary.put(createDOTTokenType());
		temporary.put(createDASHTokenType());
		temporary.put(createCARROTTokenType());
		temporary.put(createINTokenType());
		temporary.put(createDEFINEDCLASSTokenType());
		temporary.put(createEPSILONTokenType());
		temporary.put(createNEWLINETokenType());
		
		instance = new Language(temporary.alphabet, temporary.tokenTypes);
		
		UNION = instance.tokenTypes.get(UNION_NAME);
		RE_CHAR = instance.tokenTypes.get(RE_CHAR_NAME);
		CLS_CHAR = instance.tokenTypes.get(CLS_CHAR_NAME);
		OPEN_BRACKET = instance.tokenTypes.get(OPEN_BRACKET_NAME);
		CLOSE_BRACKET = instance.tokenTypes.get(CLOSE_BRACKET_NAME);
		OPEN_PARENS = instance.tokenTypes.get(OPEN_PARENS_NAME);
		CLOSE_PARENS = instance.tokenTypes.get(CLOSE_PARENS_NAME);
		PLUS = instance.tokenTypes.get(PLUS_NAME);
		STAR = instance.tokenTypes.get(STAR_NAME);
		DOT = instance.tokenTypes.get(DOT_NAME);
		DASH = instance.tokenTypes.get(DASH_NAME);
		CARROT = instance.tokenTypes.get(CARROT_NAME);
		IN = instance.tokenTypes.get(IN_NAME);
		DEFINED_CLASS = instance.tokenTypes.get(DEFINED_CLASS_NAME);
		EPSILON = instance.tokenTypes.get(EPSILON_NAME);
		NEW_LINE = instance.tokenTypes.get(NEW_LINE_NAME);
	}
	
	private static _TokenType createNEWLINETokenType() {
		_TokenType tokenType = new _TokenType(NEW_LINE_NAME);

		tokenType.stateMachine.appendCharacter('\n');
		
		return tokenType;
	}

	
	/**
	 * Constructs a TokenType for UNION tokens.
	 */
	private static _TokenType createUNIONTokenType() {
		_TokenType tokenType = new _TokenType(UNION_NAME);

		tokenType.stateMachine.appendCharacter('|');
		
		return tokenType;
	}

	/**
	 * Constructs a TokenType for RE_CHAR tokens.
	 */
	private static _TokenType createRECHARTokenType() {
		_TokenType tokenType = new _TokenType(RE_CHAR_NAME);
		
		Set<Character> validCharacters =
				RegExAlphabet.DEFAULT_ALPHABET.getAllCharacters();
		
		for (int i = 0; i < RE_CHAR_ESCAPE_CHARACTERS.length; i++) {
			validCharacters.remove(RE_CHAR_ESCAPE_CHARACTERS[i]);
		}
		tokenType.stateMachine.appendCharacterSet(validCharacters);

		_StateMachine alternate = new _StateMachine();
		alternate.appendCharacter('\\');
		
		Set<Character> alternateCharacters =
				new HashSet<Character>();
		
		for (int i = 0; i < RE_CHAR_ESCAPE_CHARACTERS.length; i++) {
			alternateCharacters.add(RE_CHAR_ESCAPE_CHARACTERS[i]);
			
		}
		alternate.appendCharacterSet(alternateCharacters);
		
		tokenType.stateMachine.unionWithStateMachine(alternate);
		
		return tokenType;
	}

	/**
	 * Constructs a TokenType for CLS_CHAR tokens.
	 */
	private static _TokenType createCLSCHARTokenType() {
		_TokenType tokenType = new _TokenType(CLS_CHAR_NAME);
		
		Set<Character> validCharacters =
				RegExAlphabet.DEFAULT_ALPHABET.getAllCharacters();
		
		for (int i = 0; i < CLS_CHAR_ESCAPE_CHARACTERS.length; i++) {
			validCharacters.remove(CLS_CHAR_ESCAPE_CHARACTERS[i]);
		}
		tokenType.stateMachine.appendCharacterSet(validCharacters);

		_StateMachine alternate = new _StateMachine();
		alternate.appendCharacter('\\');
		
		Set<Character> alternateCharacters =
				new HashSet<Character>();
		
		for (int i = 0; i < CLS_CHAR_ESCAPE_CHARACTERS.length; i++) {
			alternateCharacters.add(CLS_CHAR_ESCAPE_CHARACTERS[i]);
			
		}
		alternate.appendCharacterSet(alternateCharacters);
		
		tokenType.stateMachine.unionWithStateMachine(alternate);
		
		return tokenType;
	}

	/**
	 * Constructs a TokenType for OPEN_BRACKET tokens.
	 */
	private static _TokenType createOPENBRACKETTokenType() {
		_TokenType tokenType = new _TokenType(OPEN_BRACKET_NAME);

		tokenType.stateMachine.appendCharacter('[');
		
		return tokenType;
	}

	/**
	 * Constructs a TokenType for CLOSE_BRACKET tokens.
	 */
	private static _TokenType createCLOSEBRACKETTokenType() {
		_TokenType tokenType = new _TokenType(CLOSE_BRACKET_NAME);

		tokenType.stateMachine.appendCharacter(']');
		
		return tokenType;
	}
	
	/**
	 * Constructs a TokenType for OPEN_PARENS tokens.
	 */
	private static _TokenType createOPENPARENSTokenType() {
		_TokenType tokenType = new _TokenType(OPEN_PARENS_NAME);

		tokenType.stateMachine.appendCharacter('(');
		
		return tokenType;
	}

	/**
	 * Constructs a TokenType for CLOSE_PARENS tokens.
	 */
	private static _TokenType createCLOSEPARENSTokenType() {
		_TokenType tokenType = new _TokenType(CLOSE_PARENS_NAME);

		tokenType.stateMachine.appendCharacter(')');
		
		return tokenType;
	}
	
	/**
	 * Constructs a TokenType for PLUS tokens.
	 */
	private static _TokenType createPLUSTokenType() {
		_TokenType tokenType = new _TokenType(PLUS_NAME);

		tokenType.stateMachine.appendCharacter('+');
		
		return tokenType;
	}
	
	/**
	 * Constructs a TokenType for STAR tokens.
	 */
	private static _TokenType createSTARTokenType() {
		_TokenType tokenType = new _TokenType(STAR_NAME);

		tokenType.stateMachine.appendCharacter('*');
		
		return tokenType;
	}
	
	/**
	 * Constructs a TokenType for DOT tokens.
	 */
	private static _TokenType createDOTTokenType() {
		_TokenType tokenType = new _TokenType(DOT_NAME);

		tokenType.stateMachine.appendCharacter('.');
		
		return tokenType;
	}
	
	/**
	 * Constructs a TokenType for DASH tokens.
	 */
	private static _TokenType createDASHTokenType() {
		_TokenType tokenType = new _TokenType(DASH_NAME);

		tokenType.stateMachine.appendCharacter('-');
		
		return tokenType;
	}
	
	/**
	 * Constructs a TokenType for CARROT tokens.
	 */
	private static _TokenType createCARROTTokenType() {
		_TokenType tokenType = new _TokenType(CARROT_NAME);

		tokenType.stateMachine.appendCharacter('^');
		
		return tokenType;
	}
	
	/**
	 * Constructs a TokenType for IN tokens.
	 */
	private static _TokenType createINTokenType() {
		_TokenType tokenType = new _TokenType(IN_NAME);

		tokenType.stateMachine.appendCharacter('I');
		tokenType.stateMachine.appendCharacter('N');
		
		return tokenType;
	}
	
	/**
	 * Constructs a TokenType for DEFINED_CLASS tokens.
	 */
	private static _TokenType createDEFINEDCLASSTokenType() {
		_TokenType tokenType = new _TokenType(DEFINED_CLASS_NAME);

		tokenType.stateMachine.appendCharacter('$');
		
		Set<Character> validCharacters =
				RegExAlphabet.DEFAULT_ALPHABET.getAllCharacters();
		
		_StateMachine followingStateMachine = new _StateMachine();
		
		for (int i = 0; i < DEFINED_CLASS_INVALID_CHARACTERS.length; i++) {
			validCharacters.remove(DEFINED_CLASS_INVALID_CHARACTERS[i]);
		}
		followingStateMachine.appendCharacterSet(validCharacters);
		followingStateMachine.repeatZeroOrMore();
		
		tokenType.stateMachine.appendStateMachine(followingStateMachine);

		return tokenType;
	}

	/**
	 * Constructs a TokenType for EPSILON tokens.
	 */
	private static _TokenType createEPSILONTokenType() {
		_TokenType tokenType = new _TokenType(EPSILON_NAME);

		return tokenType;
	}
}