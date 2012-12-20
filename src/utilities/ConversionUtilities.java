package utilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import language._State;
import language._StateMachine;
import language._TokenType;
import grammar._Rule;
import immutable.Language;
import immutable.Rule;
import immutable.State;
import immutable.StateMachine;
import immutable.TokenType;

/**
 * This class contains static methods that simplify converting between mutable
 * and immutable instances of objects.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class ConversionUtilities {

	public static Map<_State, State> convertStates(Collection<_State> oldStates) {
		// Construct a Map of old State objects to new State objects and
		// initialize the result 
		HashMap<_State, State> stateMap = new HashMap<_State, State>();
		for (_State key : oldStates) {
			State value = new State(
					key.name,
					new HashMap<Character, Set<State>>(),
					new HashMap<State, Set<Character>>(),
					new HashSet<State>());
			stateMap.put(key, value);
		}
		
		// Hydrate the stateToCharSetMapping and charToStateSetMapping fields
		for (_State oldState : stateMap.keySet()) {
			// Get a reference to the corresponding new State object
			State newState = stateMap.get(oldState);

			// Hydrate transitions
			for (_State oldDest : oldState.stateToCharSetMap.keySet()) {
				State newDest = stateMap.get(oldDest);
				Set<Character> characters =
						oldState.getCharactersToDestinations(oldDest);
				newState.addTransitions(characters, newDest);
			}
			
			// Hydrating epsilonTransitions
			for (_State epsilonTransition : oldState.epsilonTransitions) {
				if (stateMap.get(epsilonTransition) == null) {
					System.out.println("No equivalent state found.");
					continue;
				}
				newState.epsilonTransitions.add(stateMap.get(epsilonTransition));
			}
		}

		// At this point, all of the new State objects still have mutable
		// fields. Here, we reconstruct the final mapping, making everything
		// immutable.
		HashMap<_State, State> result = new HashMap<_State, State>();
		for (_State oldState : stateMap.keySet()) {
			// Get a reference to the new State object
			State mutableNewState = stateMap.get(oldState);
			State newState = new State(
					mutableNewState.name,
					mutableNewState.charToStateSetMap,
					mutableNewState.stateToCharSetMap,
					mutableNewState.epsilonTransitions);
			result.put(oldState, newState);
		}

		return result;
	}

	public static Map<_Rule, Rule> convertRules(Collection<_Rule> oldRules) {

		// Construct a Map of old _Rule objects to new Rule objects and
		// initialize the result 
		HashMap<_Rule, Rule> ruleMapping = new HashMap<_Rule, Rule>();
		for (_Rule key : oldRules) {
			ruleMapping.put(key, new Rule(key.name,
					new ArrayList<ArrayList<Object>>(),
					new HashSet<TokenType>()));
		}

		// Hydrate the rules list
		for (_Rule oldRule : oldRules) {
			Rule newRule = ruleMapping.get(oldRule);

			for (List<Object> oldChain : oldRule.possibleChildren) {
				// Initialize the new chain
				ArrayList<Object> newChain = new ArrayList<Object>();

				// Iterate over the chains
				for (Object item : oldChain) {

					if (item == null) {
						String msg = oldRule.name + " contains a null value.";
						ErrorUtilities.throwError(msg);
					}
					
					// If there's something unexpected, fail loud
					if (item.getClass() != TokenType.class &&
							item.getClass() != _Rule.class) {
						String msg = "Expected a String or _Rule object, but " +
							"got a " + item.getClass().getCanonicalName() +
							" instead.";
						ErrorUtilities.throwError(msg);
					}
					
					// Handle TokenType case
					if (item.getClass() == TokenType.class) {
						TokenType tokenType = (TokenType)item;
						newChain.add(tokenType);
					}

					// Handle Rule case
					if (item.getClass() == _Rule.class) {
						_Rule rule = (_Rule)item;
						newChain.add(ruleMapping.get(rule));
					}
				}
				newRule.possibleChildren.add(newChain);
			}
		}

		// At this point, all of the new Rule objects still have mutable
		// fields. Here, we reconstruct the final mapping, making everything
		// immutable.
		HashMap<_Rule, Rule> result = new HashMap<_Rule, Rule>();
		for (_Rule oldRule : ruleMapping.keySet()) {
			// Get a reference to the new State object
			Rule mutableNewRule = ruleMapping.get(oldRule);
			Rule newState = new Rule(
					mutableNewRule.name,
					mutableNewRule.possibleChildren,
					mutableNewRule.startingTokenTypes);
			result.put(oldRule, newState);
		}
		
		return result;
	}
	
	public static Map<_TokenType, TokenType> ConvertTokenType(
			Collection<_TokenType> oldTokenTypes) {
		// Construct a Map of old _TokenType objects to new TokenType objects
		// and initialize the result
		HashMap<_TokenType, TokenType> tokenTypeMap =
				new HashMap<_TokenType, TokenType>();
		for (_TokenType oldTokenType : oldTokenTypes) {
			
			Language language = oldTokenType.language;
			String name = oldTokenType.name;
			StateMachine stateMachine = ConversionUtilities.convertStateMachine(
					oldTokenType.stateMachine);
			boolean reservedWord = oldTokenType.reservedWord;
			
			
			TokenType tokenType = new TokenType(language, name,
					stateMachine, reservedWord);
			
			tokenTypeMap.put(oldTokenType, tokenType);
		}
		return tokenTypeMap;
	}

	public static StateMachine convertStateMachine(
			_StateMachine stateMachine) {
		
		Map<_State, State> stateMap = convertStates(stateMachine.states);
		
		State initialState = stateMap.get(stateMachine.initialState);
		State finalState = stateMap.get(stateMachine.finalState);
		List<State> states = new ArrayList<State>();
		for (_State oldState : stateMachine.states) {
			states.add(stateMap.get(oldState));
		}
		
		Set<Character> startingCharacters =
				stateMachine.getStartingCharacters();
		
		return new StateMachine(initialState, finalState, states,
				startingCharacters);
	}
}
