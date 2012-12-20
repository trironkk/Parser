package generators;

import immutable.Language;
import immutable.Report;
import immutable.TokenType;
import immutable.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import language._StateMachine;
import language._TokenType;
import language.defaults.RegExAlphabet;
import language.defaults.RegExLanguage;

import utilities.CopyUtilities;
import utilities.ErrorUtilities;
import utilities.LogUtilities;
import utilities.StringUtilities;


public class LanguageGenerator {
	
	public static Language generate(Report report) {
		// Get the stubs of the TokenTypes
		Map<String, _TokenType> tokenTypes = generateStubs(report);
		
		// Process all _TokenType objects
		for (_TokenType tokenType : tokenTypes.values()) {
			
			// If it's already been processed, skip it
			if (tokenType.unprocessedTokens.isEmpty()) {
				continue;
			}
			
			processTokenType(tokenType, tokenTypes);
		} 
		
		// Remove all tokenTypes that are not definitions
		Map<String, _TokenType> finalTokenTypes =
				new HashMap<String, _TokenType>();
		for (_TokenType tokenType: tokenTypes.values()) {
			if (tokenType.tokenDefinition) {
				finalTokenTypes.put(tokenType.name, tokenType);
			}
		}
		tokenTypes = finalTokenTypes;
		
		return new Language(RegExAlphabet.DEFAULT_ALPHABET, tokenTypes);
	}
	
	/**
	 * Processed a particular _TokenType object, and all _TokenType objects that
	 * depend on it.
	 */
	private static void processTokenType(_TokenType currentTokenType,
			Map<String, _TokenType> tokenTypes) {
		// Continue processing tokens until there are no more unprocessed tokens
		LogUtilities.logln("Processing " + currentTokenType.name);
		while (currentTokenType.unprocessedTokens.isEmpty() == false) {
			
			// Process the next expression
			_StateMachine stateMachine =
					processREXP(currentTokenType, tokenTypes);
			
			// Update the current token type's definition
			currentTokenType.stateMachine.appendStateMachine(stateMachine);
		}
		LogUtilities.logln("Finished processing " + currentTokenType.name);
	}
	
	/**
	 * An overload to processExpression that just transparently calls the
	 * original passing false for parenthetical
	 */
	private static _StateMachine processREXP(_TokenType currentTokenType,
			Map<String, _TokenType> tokenTypes) {
		return processREXP(currentTokenType, tokenTypes, false);
	}

	/**
	 * This method addresses any token that can be the first match out of a
	 * rexp rule.
	 * 
	 * Note: This method handles the following TokenTypes:
	 * 	OPEN_PARENS_TOKEN
	 * 	OPEN_PARENS_TOKEN
	 * 	DOT_TOKEN
	 * 	OPEN_BRACKET_TOKEN
	 * 	DEFINED_CLASS_TOKEN
	 */
	private static _StateMachine processREXP(_TokenType currentTokenType,
			Map<String, _TokenType> tokenTypes, boolean parenthetical) {
		LogUtilities.logln("Processing next expression: " +
				currentTokenType.unprocessedTokens.get(0).tokenType.name + " " +
				currentTokenType.unprocessedTokens.get(0).contents);
		
		_StateMachine stateMachine = new _StateMachine();
		
		// Extract the first unprocessed Token
		Token firstToken = currentTokenType.popToken();
		
		// Split functionality according to TokenType
		if (firstToken.tokenType == RegExLanguage.RE_CHAR) {
			
			// Add the last character. Either it's the first character, or it's
			// the appropriate character in an escape sequence.
			int lastIndex = firstToken.contents.length() - 1;
			Character target = firstToken.contents.charAt(lastIndex); 
			stateMachine.appendCharacter(target);
			
		} else if (firstToken.tokenType == RegExLanguage.OPEN_PARENS) {
			
			// Append the result of calling processREXP again, expecting a
			// CLOSE_PARENS token
			stateMachine =
					processREXP(currentTokenType, tokenTypes, true);
			
			currentTokenType.reservedWord = false;
			
		} else if (firstToken.tokenType == RegExLanguage.DOT) {
			
			currentTokenType.reservedWord = false;
			
			// Append a wild card transition
			stateMachine.appendCharacter(null);
			
		} else if (firstToken.tokenType == RegExLanguage.OPEN_BRACKET) {
			
			currentTokenType.reservedWord = false;
			
			Set<Character> range =
					processBrackets(currentTokenType, tokenTypes);
			stateMachine.appendCharacterSet(range);
			
		} else if (firstToken.tokenType == RegExLanguage.DEFINED_CLASS) {
			
			currentTokenType.reservedWord = false;
			
			_TokenType targetTokenType = tokenTypes.get(firstToken.contents);
			
			if (targetTokenType == null) {
				String msg = "No token type \"" + firstToken.contents + "\" " +
						"has been defined.";
				ErrorUtilities.throwError(msg);
			}
			
			// Ensure that the other TokenType has been initialized
			if (targetTokenType.unprocessedTokens.size() > 0) {
				processTokenType(targetTokenType, tokenTypes);
			}
			
			// Duplicate the state machine that defines the targetTokenType
			_StateMachine targetStateMachine =
					CopyUtilities.CopyStateMachine(
							targetTokenType.stateMachine);
			
			stateMachine.appendStateMachine(targetStateMachine);
			
		} else {
			String msg = "Expected one of the following: OPEN_PARENS, " +
					"OPEN_PARENS, DOT, OPEN_BRACKET, " +
					"DEFINED_CLASS.\nInstead got: " +
					firstToken.tokenType.name + ".";
			ErrorUtilities.throwError(msg);
		}
		
		// Handle PLUS_TOKEN and STAR_TOKEN
		stateMachine = processRepeats(stateMachine, currentTokenType);
		
		// Handle UNION_TOKEN
		if (currentTokenType.frontTokenType() == RegExLanguage.UNION) {
			
			currentTokenType.reservedWord = false;
			
			currentTokenType.popToken();
			_StateMachine other =
					processREXP(currentTokenType, tokenTypes);
			stateMachine.unionWithStateMachine(other);
		}
		
		// Handle CLOSE_PARENS_TOKEN
		if (parenthetical) {
			firstToken = currentTokenType.popToken();
			if (firstToken.tokenType != RegExLanguage.CLOSE_PARENS) {
				String msg = "Expected CLOSE_PARENS, but instead got " +
						firstToken.tokenType.toString() + ".\n";
				msg += currentTokenType.name + "\n";
				msg += stateMachine.toString();
				ErrorUtilities.throwError(msg);
			}
		}
		
		return stateMachine;
	}

	/**
	 * This method modifies the state machine to match repeater tokens.
	 * 
	 * Note: This method handles the following TokenTypes:
	 * 	STAR_TOKEN
	 * 	PLUS_TOKEN
	 */
	private static _StateMachine processRepeats(_StateMachine stateMachine,
			_TokenType currentTokenType) {

		TokenType front = currentTokenType.frontTokenType();
		
		// Split functionality according to TokenType
		if (front == RegExLanguage.STAR) {
			
			currentTokenType.reservedWord = false;
			
			// Modify the State Machine to match itself zero or more times 
			stateMachine.repeatZeroOrMore();
			
			currentTokenType.popToken();
			
		} else if (front == RegExLanguage.PLUS) {

			currentTokenType.reservedWord = false;
			
			// Create a duplicate of this state machine that will match itself
			// zero or more times
			_StateMachine duplicate =
					CopyUtilities.CopyStateMachine(stateMachine);
			
			duplicate.repeatZeroOrMore();

			// Append that duplicated state machine
			stateMachine.appendStateMachine(duplicate);
			
			currentTokenType.popToken();
		}
		
		return stateMachine;
	}
	
	/**
	 * Returns a set of the characters that an OPEN_BRACKET defines.
	 */
	private static Set<Character> processBrackets(_TokenType currentTokenType,
			Map<String, _TokenType> tokenTypes) {

		Set<Character> result = new HashSet<Character>();
		
		TokenType frontTokenType = currentTokenType.frontTokenType();
		
		if (frontTokenType == RegExLanguage.CARROT) {
			
			// Remove the CARROT_TOKEN
			currentTokenType.popToken();
			
			Set<Character> excludedRange =
					processCharSet(currentTokenType);
			Set<Character> baseRange = null;

			// Remove the IN_TOKEN
			currentTokenType.popToken();

			TokenType nextTokenType = currentTokenType.frontTokenType();
			
			if (nextTokenType == RegExLanguage.OPEN_BRACKET) {

				// Remove the OPEN_BRACKET_TOKEN
				currentTokenType.popToken();
				
				baseRange = processCharSet(currentTokenType);
				
			} else if (nextTokenType == RegExLanguage.DEFINED_CLASS) {
				
				Token nextToken = currentTokenType.popToken();
				
				_TokenType targetTokenType = tokenTypes.get(nextToken.contents);
				
				// Ensure that the other TokenType has been initialized
				if (targetTokenType.unprocessedTokens.isEmpty() == false) {
					processTokenType(targetTokenType, tokenTypes);
				}
				
				baseRange =
						targetTokenType.stateMachine.getStartingCharacters();
			}
			
			result.addAll(baseRange);
			result.removeAll(excludedRange);
			
		} else if (frontTokenType == RegExLanguage.CLS_CHAR) {
			
			result.addAll(processCharSet(currentTokenType));
			
		} 
		
		return result;
	}

	private static Set<Character> processCharSet(_TokenType currentTokenType) {
		// Initialize variables
		Set<Character> result = new HashSet<Character>();

		Token firstToken = currentTokenType.unprocessedTokens.get(0);
		Token secondToken = currentTokenType.unprocessedTokens.get(1);
		Token thirdToken = currentTokenType.unprocessedTokens.get(2);

		if (firstToken.tokenType != RegExLanguage.CLS_CHAR) {
			String msg = "Expected CLS_CHAR, but got " +
					firstToken.tokenType.name + " instead.";
			ErrorUtilities.throwError(msg);
		}
		
		if (secondToken.tokenType == RegExLanguage.CLOSE_BRACKET) {
			
			result.add(StringUtilities.getUnescapedCharacter(
					firstToken.contents));

			// Pop off the first and second tokens
			currentTokenType.popToken();
			currentTokenType.popToken();
			
			return result;
			
		} else if (secondToken.tokenType == RegExLanguage.DASH) {
			
			if (thirdToken.tokenType != RegExLanguage.CLS_CHAR) {
				String msg = "Expected CLS_CHAR, but got " +
						thirdToken.tokenType.name + "instead.";
				ErrorUtilities.throwError(msg);
			}
			
			// Get the bounds
			char firstChar = StringUtilities.getUnescapedCharacter(
					firstToken.contents);
			char secondChar = StringUtilities.getUnescapedCharacter(
					thirdToken.contents);
			
			// Add the range to the result set
			while (firstChar <= secondChar) {
				result.add(firstChar);
				firstChar++;
			}
			
			// Pop off the first, second, and third tokens
			currentTokenType.popToken();
			currentTokenType.popToken();
			currentTokenType.popToken();
			
			if (currentTokenType.frontTokenType() ==
					RegExLanguage.CLS_CHAR) {
				
				// Call again for the case of multiple consecutive ranges
				result.addAll(processCharSet(currentTokenType));
				
			} else {
				Token lastToken = currentTokenType.popToken();
				if (lastToken.tokenType != RegExLanguage.CLOSE_BRACKET) {
					String msg = "Expected CLOSE_BRACKET, but got " +
							firstToken.tokenType.name + "instead.";
					ErrorUtilities.throwError(msg);
				}
			}
			return result;
		} else {
			
			String msg = "Expected either CLOSE_BRACKET or DASH, " +
					"but got " + firstToken.tokenType.name + "instead.";
			ErrorUtilities.throwError(msg);
			return null;
		}
	}
	
	/**
	 * Constructs a list of undefined TokenType objects from a set of Tokens
	 * specifying a regular expression for a token. Also marks all required
	 * token definitions to be included in the generated language.
	 */
	private static Map<String, _TokenType> generateStubs(Report report) {
		// Initialize the resulting list
		List<_TokenType> tokenTypeStubs = new ArrayList<_TokenType>();
		tokenTypeStubs.add(new _TokenType("Token" + tokenTypeStubs.size()));
		
		// Iterate over all the tokens in the Report
		for (Token token : report.tokens) {
			if (token.tokenType == RegExLanguage.NEW_LINE) {
				tokenTypeStubs.add(new _TokenType("Token" +
						tokenTypeStubs.size()));
				continue;
			}
			
			// Get a reference to the last element in tokenTypeStubs
			_TokenType last = tokenTypeStubs.get(tokenTypeStubs.size() - 1);
			
			// Add this Token to the last element of the result
			last.unprocessedTokens.add(token);
		}
		
		// Remove all empty _TokenType objects generated by trailing or
		// back-to- back NEW_LINE tokens. Also, after finding an empty stub,
		// mark all following token types as token definitions.
		List<_TokenType> temporary = new ArrayList<_TokenType>();
		boolean include = false;
		for (int i = 0; i < tokenTypeStubs.size(); i++) {
			_TokenType tokenType = tokenTypeStubs.get(i);
			if (tokenType.unprocessedTokens.size() > 0) {
				tokenType.tokenDefinition = include;
				temporary.add(tokenType);
			} else {
				include = true;
			}
		}
		tokenTypeStubs = temporary;
		
		// Record the names of these tokenTypes
		Set<String> names = new HashSet<String>();
		for (_TokenType tokenType : tokenTypeStubs) {
			
			// Record the name
			tokenType.name = tokenType.unprocessedTokens.get(0).contents;
			
			// Ensure that no other tokens have the same name
			if (names.contains(tokenType.name)) {
				String msg = tokenType.name + " is defined more than once.";
				ErrorUtilities.throwError(msg);
			}
			
			// Add that name to the list of names
			names.add(tokenType.name);
			
			// Remove that Token from the list of unprocessed Tokens
			tokenType.unprocessedTokens.remove(0);
		}
		
		// Construct a resulting mapping
		Map<String, _TokenType> result = new HashMap<String, _TokenType>();
		for (_TokenType tokenType : tokenTypeStubs) {
			result.put(tokenType.name, tokenType);
		}
		
		return result;
	}
}
