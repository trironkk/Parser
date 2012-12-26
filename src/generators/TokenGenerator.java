package generators;

import java.util.ArrayList;
import java.util.List;

import immutable.State;
import immutable.TokenType;
import language.defaults.RegExLanguage;
import immutable.Token;
import utilities.ErrorUtilities;
import utilities.LogUtilities;
import utilities.StringUtilities;

public class TokenGenerator {
	
	public static Token generate(TokenType tokenType, StringBuffer contents) {
		// Initialize bookkeeping 
		String tokenContents = new String();
		List<State> visitedStates = new ArrayList<State>();
		State finalState = tokenType.stateMachine.finalState;
		State currentState = tokenType.stateMachine.initialState;
		
		// Repeat until we hit a terminal condition
		while (true) {
			// Stop if we reach the end of the file
			if (contents.length() == 0) {
				break;
			}
			
			// Peek at the next character
			Character nextChar = contents.charAt(0);
			
			// Stop if the current state cannot transition
			if (currentState.getDestinations(nextChar).size() == 0) {
				break;
			}
			
			// Consume the next character
			contents.deleteCharAt(0);
			visitedStates.add(currentState);
			tokenContents += nextChar;
			
			// TODO: Implement NFA traversal.
			if (currentState.getDestinations(nextChar).size() > 1) {
				ErrorUtilities.throwError(
						"We do not support NFA traversal yet.");
			}
			currentState =
					currentState.getDestinations(nextChar).iterator().next();
		}

        // Step backwards through the visited State objects until there
        // one of them is an accept state, or the visitedStates list is
        // empty.
        while (finalState != currentState) {
        	// If this state has an epsilon transition to the final state, we're
        	// done.
        	if (currentState.epsilonTransitions.contains(finalState)) {
        		break;
        	}
        	
        	// TODO: Remove hack equals check...
        	if (finalState.name.equals(currentState.name))
        		break;
        	
        	if (visitedStates.isEmpty()) {
        		break;
//        		ErrorUtilities.throwError("Failed to find a matched string.");
        	}
        	int visitedLastIndex = visitedStates.size()-1;
        	currentState = visitedStates.get(visitedLastIndex);
        	visitedStates.remove(visitedLastIndex);
        	
        	int tokenContentsLastIndex = tokenContents.length() - 1;
        	contents.insert(0, tokenContents.charAt(tokenContentsLastIndex));
        	tokenContents = tokenContents.substring(0, tokenContentsLastIndex);
        }
        
		// If we found an accept state, return a new Token object. Otherwise,
		// return null.
    	// TODO: Remove hack equals check...
		if (finalState == currentState ||
        		currentState.epsilonTransitions.contains(finalState) ||
        		finalState.name.equals(currentState.name)) {
			return new Token(tokenType, tokenContents);
		} else {
			// TODO: Make this actually halt. We can't make it halt here until
			// we reimplement the getTokenLength logic.
			String msg = "contents did not start with " + tokenType.name +
					"\nContents: " +
					StringUtilities.escaped(contents.toString());
//			ErrorUtilities.throwError(msg);
			LogUtilities.logln(msg);
			return new Token(RegExLanguage.EPSILON, "");
		}
	}
	
	public static int getTokenLength(TokenType token, StringBuffer contents) {
		// TODO: Reimplement the logic in generate to make this method more
		// performant.
		StringBuffer duplicateContents = new StringBuffer(contents.toString());
		Token resultingToken = generate(token, duplicateContents);
		LogUtilities.logln("Length of the next " + token.name + ":\n" +
				resultingToken.contents.length());
		return resultingToken.contents.length();
	}
}
