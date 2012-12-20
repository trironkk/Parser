package generators;

import immutable.Grammar;
import immutable.Rule;
import immutable.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import language.defaults.RegExLanguage;

import immutable.Report;
import immutable.Token;

import utilities.ErrorUtilities;
import utilities.LogUtilities;
import utilities.StringUtilities;

/**
 * This class handles the generation of a Report from a string given a grammar
 * specification.
 * 
 * Note: This assumes that the grammar specification is unambiguous.
 *
 * @author Trironk Kiatkungwanglai
 */
public class ReportGenerator {
	
	/**
	 * This is the exposed method call. All this function does is to call
	 * recursiveDescent and verify that that method does not return an invalid
	 * result.
	 */
	public static Report generate(Grammar grammar, String rawContents) {
		// Get a StringBuffer object, to allow for passing a string's contents
		// around by reference, rather than by value.
		StringBuffer contents = new StringBuffer(rawContents);

		// Call the recursive method
		List<Token> tokens = recursiveDescent(grammar.root, contents);
		
		// If descend returns null, there was a syntax error. Throw an
		// exception.
		if (tokens == null) {
			String errorMessage =
					"The root call of recursiveDescent returned null.";
			ErrorUtilities.throwError(errorMessage);
		}

		// If not all the contents were consumed, there was a syntax error.
		// Throw an exception.
		if (contents.length() > 0) {
			String errorMessage =
					"Failed to parse all of the contents.";
			errorMessage += "\n  contents remaining: \"" +
					StringUtilities.escaped(contents.toString()) + "\"";
			errorMessage += "\n  tokens parsed:      " + tokens;
			ErrorUtilities.throwError(errorMessage);
		}
		
		return new Report(grammar, tokens);
	}

	/**
	 * This function does the work. It recursively descends through the grammar,
	 * recording tokens as it goes.
	 */
	private static List<Token> recursiveDescent(Rule rule,
			StringBuffer contents) {
		// Remove leading whitespace
		StringUtilities.removeLeadingWhitespace(contents);
		
		// Return an empty list if we've reached the end of the string
		if (contents.length() == 0) {
			return new ArrayList<Token>();
		}
		
		LogUtilities.logln(StringUtilities.padRight(rule.name + ":", 30) +
				"\"" + StringUtilities.escaped(contents.toString() +
				"\""));
		
		// Initialize the results
		List<Token> result = new ArrayList<Token>();
		
		// Extract the first character
		Character firstCharacter = contents.charAt(0);
		
		// If this rule cannot parse this string, return an empty list to
		// signify that the parent node should pick a different chain.
		Set<TokenType> nextTokenTypes = new HashSet<TokenType>();
		for (TokenType expectedTokenType : rule.startingTokenTypes) {
			Set<Character> startingCharacters =
					expectedTokenType.stateMachine.startingCharacters; 
			if (startingCharacters.contains(firstCharacter)) {
				nextTokenTypes.add(expectedTokenType);
			}
		}
		
		// Get the actual next token. In the event that more than one token is
		// possible, choose the longest token.
		// TODO: Incorporate the information from this computation into the
		// overarching flow of this method to prevent recomputation.
		TokenType nextTokenType = null;
		if (nextTokenTypes.size() > 1) {
			// Compute the lengths of all possible token types
			Map<Integer, Set<TokenType>> tokenTypeLengthMap =
					new HashMap<Integer, Set<TokenType>>();
			for (TokenType tokenType : nextTokenTypes) {
				int length = TokenGenerator.getTokenLength(tokenType, contents);
				if (tokenTypeLengthMap.containsKey(length) == false) {
					tokenTypeLengthMap.put(length, new HashSet<TokenType>());
				}
				tokenTypeLengthMap.get(length).add(tokenType);
			}
			// Get the longest length
			int length = Integer.MIN_VALUE;
			for (int currentLength : tokenTypeLengthMap.keySet()) {
				if (length < currentLength) {
					length = currentLength;
				}
			}
			
			// Handle ambiguity
			if (tokenTypeLengthMap.get(length).size() > 1) {
				
				Set<TokenType> ambiguousTokenTypes = new HashSet<TokenType>();
				ambiguousTokenTypes.addAll(
						tokenTypeLengthMap.get(length));
				Set<TokenType> reservedWordTokens = new HashSet<TokenType>();
				for (TokenType ambiguousTokenType : ambiguousTokenTypes) {
					if (ambiguousTokenType.reservedWord) {
						reservedWordTokens.add(ambiguousTokenType);
					}
				}
				
				if (reservedWordTokens.size() == 1) {
					nextTokenType = reservedWordTokens.iterator().next();
				} else {
					String msg = "";
					if (length == 0) {
						msg += "None of the following token types can start " +
								"the remaining string: [ ";
					} else {
						msg += "Ambiguous next token. Could be any of the " + 
							"following: [";
					}
					
					for (TokenType token : tokenTypeLengthMap.get(length)) {
						msg += token.name + ' ';
					}
					msg += "]\n\nContents: " +
							StringUtilities.escaped(contents.toString());
					ErrorUtilities.throwError(msg);
				}
			} else {
				nextTokenType =
						tokenTypeLengthMap.get(length).iterator().next();
			}
		} else if (nextTokenTypes.size() == 1) {
			nextTokenType = nextTokenTypes.iterator().next();
		} else {
			String msg = 
					"Potential Syntax error: Expected one of the following " +
					"tokens: [ ";
			for (TokenType startingTokenType: rule.startingTokenTypes) {
				msg += startingTokenType.name + ' ';
			}
			msg += "]\n\nContents: " +
					StringUtilities.escaped(contents.toString());
			
			msg += "\n\nRule: " + rule.name;
			LogUtilities.logln(msg);
			
			if (rule.startingTokenTypes.contains(RegExLanguage.EPSILON)
					== false) {
				// TODO: This is a hack for determining if any of the starting
				// token types are equivalent to RegExLanguage.EPSILON.
				for (TokenType tokenType : rule.startingTokenTypes) {
					if (tokenType.stateMachine.states.size() == 1) {
						return new ArrayList<Token>();
					}
				}
				ErrorUtilities.throwError(msg);
			}
			
			return new ArrayList<Token>();
		}
		
		// Get the possible chains that can match the first character of this
		// string
		List<ArrayList<Object>> possibleChildren =
				new ArrayList<ArrayList<Object>>();
		
		for (ArrayList<Object> possibleChain : rule.possibleChildren) {
			Object firstElement = possibleChain.get(0);
			
			// Handle the case of finding a TokenType first
			if (firstElement.getClass() == TokenType.class) {
				TokenType firstItem = (TokenType)firstElement;
				if (nextTokenType == firstItem) {
					possibleChildren.add(possibleChain);
				}
			}
			
			// Handle the case of finding a Rule first
			if (firstElement.getClass() == Rule.class) {
				Rule firstItem = (Rule)firstElement;
				if (firstItem.startingTokenTypes.contains(nextTokenType)) {
					possibleChildren.add(possibleChain);
				}
			}
		}
		
		// If multiple possible children are detected, the grammar is ambiguous.
		if (possibleChildren.size() > 1) {
			String msg = "Ambiguous grammar detected. Possible chains:\n";
			for (ArrayList<Object> chain : possibleChildren) {
				msg += "[ ";
				for (Object currentChild : chain) {
					if (currentChild.getClass() == TokenType.class) {
						msg += ((TokenType)currentChild).name + ' ';
					}
					if (currentChild.getClass() == Rule.class) {
						msg += ((Rule)currentChild).name + ' ';
					}
				}
				msg += "]\n";
			}
			msg += "\n\nContents: " +
					StringUtilities.escaped(contents.toString());
			ErrorUtilities.throwError(msg);
		}
		
		// If no possible children are detected, the grammar's starting
		// characters were inappropriately generated.
		if (possibleChildren.size() == 0) {
			ErrorUtilities.throwError("No valid possible children detected.");
		}
		
		// Extract the child rule
		List<Object> children = possibleChildren.get(0);
		
		LogUtilities.log("Intending to match: ");
		for (Object currentChild : children) {
			if (currentChild.getClass() == TokenType.class) {
				LogUtilities.log(((TokenType)currentChild).name + ' ');
			}
			if (currentChild.getClass() == Rule.class) {
				LogUtilities.log(((Rule)currentChild).name + ' ');
			}
		}
		LogUtilities.logln();
		
		// Iterator over the children, recursively calling this method on each
		// of them.
		for (Object currentChild : children) {

			// Handle the case of finding a TokenType
			if (currentChild.getClass() == TokenType.class) {
				StringUtilities.removeLeadingWhitespace(contents);
				LogUtilities.logln("Now matching the " +
						((TokenType)currentChild).name + " TokenType.\t" +
						StringUtilities.escaped(contents.toString()));
				
				result.add(TokenGenerator.generate(
						(TokenType)currentChild, contents));
			}
			
			// Handle the case of finding a Rule
			if (currentChild.getClass() == Rule.class) {
				Rule childRule = ((Rule)currentChild);
				List<Token> tokens =
						recursiveDescent(childRule, contents);
				result.addAll(tokens);
				if (childRule.startingTokenTypes.contains(null) == false &&
						tokens.size() == 0) {
					
					LogUtilities.log(childRule.name + ": [");
					for (TokenType tokenType : childRule.startingTokenTypes) {
						LogUtilities.log(tokenType.name + ' ');
					}
					LogUtilities.logln(']');
					LogUtilities.logln(
							StringUtilities.escaped(
									contents.toString()));
				}
				LogUtilities.logln(((Rule)currentChild).name + " returned " +
						tokens.size() + " token(s).");
			}
		}
		
		// Return the results
		return result;
	}
}
