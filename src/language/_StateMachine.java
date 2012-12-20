package language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import language.defaults.RegExAlphabet;
import utilities.CopyUtilities;
import utilities.ErrorUtilities;
import utilities.StringUtilities;

/**
 * Associates an initial, final state, a list of involved states, and the
 * related Token for a particular component
 * 
 * @author Trironk Kiatkungwanglai
 */
public class _StateMachine {

	/**
	 * The initial state.
	 */
	public _State initialState;

	/**
	 * The final state.
	 */
	public _State finalState;

	/**
	 * A list of involved states.
	 */
	public List<_State> states;

	/**
	 * Standard constructor.
	 */
	public _StateMachine(_State initialState, _State finalState) {
		this.initialState = initialState;
		this.finalState = finalState;
		this.states = new ArrayList<_State>();
	}

	/**
	 * Blank constructor.
	 */
	public _StateMachine() {
		this.states = new ArrayList<_State>();
		this.initialState = createState();
		this.finalState = this.initialState;
	}
	
	/**
	 * Copy constructor.
	 */
	public _StateMachine(_StateMachine original) {
		_StateMachine duplicate = CopyUtilities.CopyStateMachine(original);
		this.initialState = duplicate.initialState;
		this.finalState = duplicate.finalState;
		this.states = duplicate.states;
	}

	/**
	 * Create a new state.
	 */
	public _State createState() {
		_State result = new _State(this.states.size());
		this.states.add(result);
		return result;
	}

	/**
	 * Appends a set of characters to this state machine.
	 */
	public void appendCharacter(Character c) {
		Set<Character> set = new HashSet<Character>();
		set.add(c);
		appendCharacterSet(set);
	}
	
	/**
	 * Appends a set of characters to this state machine.
	 */
	public void appendString(String str) {
		for (Character c : str.toCharArray()) {
			Set<Character> set = new HashSet<Character>();
			set.add(c);
			appendCharacterSet(set);
		}
	}

	/**
	 * Appends a set of characters to this state machine.
	 */
	public void appendCharacterSet(Set<Character> characterSet) {
		if (characterSet.size() == 0) {
			return;
		}
		// Create the new state
		_State s1 = createState();

		// Add transitions
		for (Character c : characterSet) {
			this.finalState.addTransition(c, s1);
		}
		// Update the final state
		this.finalState = s1;
	}

	/**
	 * Appends a set of characters to this state machine.
	 */
	public void repeatZeroOrMore() {
		// Create the new state
		_State s0 = createState();
		_State s3 = createState();

		// Add transitions
		s0.epsilonTransitions.add(this.initialState);
		s0.epsilonTransitions.add(s3);
		this.finalState.epsilonTransitions.add(s3);
		s3.epsilonTransitions.add(this.initialState);

		// Update the initial and final state
		this.initialState= s0;
		this.finalState = s3;
	}

	/**
	 * Append specified state machine to the end of this state machine.
	 */
	public void appendStateMachine(_StateMachine other) {
		this.finalState.epsilonTransitions.add(other.initialState);
		this.finalState = other.finalState;
		for (_State s : other.states) {
			s.name = "State" + this.states.size();
			this.states.add(s);
		}
	}

	/**
	 * 
	 */
	public void unionWithStateMachine(_StateMachine other) {
		// Create states
		_State newInitial = createState();
		_State newFinal = createState();

		// Add transitions
		newInitial.epsilonTransitions.add(this.initialState);
		newInitial.epsilonTransitions.add(other.initialState);

		this.finalState.epsilonTransitions.add(newFinal);
		other.finalState.epsilonTransitions.add(newFinal);

		// Adding the other state machine's states
		for (_State state : other.states) {
			state.name = "State" + this.states.size();
			states.add(state);
		}

		// Update the initial and final states
		this.initialState = newInitial;
		this.finalState = newFinal;
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		String result = new String();
		result += "Initial State: " + this.initialState.name;
		result += "\nFinal State: " + this.finalState.name;
		result += "\nStates:\n";
		Iterator<_State> statesIterator = states.iterator();
		while (statesIterator.hasNext()) {
			result += StringUtilities.tabify(statesIterator.next().toString());
			result += '\n';
		}

		return result;
	}

	/**
	 * Returns a set of characters that can begin a string that matches this
	 * TokenType.
	 */
	public Set<Character> getStartingCharacters() {
		Set<Character> includedCharacters = new HashSet<Character>();

		// Get a mapping of all transition states to a set of characters
		// associate with them. This is non-trivial because of the null
		// character.
		Set<_State> nullStates = new HashSet<_State>();
		Map<_State, Set<Character>> transitions =
				new HashMap<_State, Set<Character>>();
		for (_State dest : this.initialState.getDestinations()) {
			Set<Character> charactersToDest =
					this.initialState.getCharactersToDestinations(dest); 
			transitions.put(dest, charactersToDest);
			if (transitions.get(dest).contains(null)) {
				nullStates.add(dest);
			}
		}

		// If there are multiple State objects with a null transition
		// Character, we need to alert the user.
		if (nullStates.size() > 1) {
			String msg = "";
			msg +="Multiple null transitions detected in the initial state of ";
			msg += this.initialState.name;
			msg += ".";
			ErrorUtilities.throwError(msg);
		}

		// Perform a naive search over the transition states for accept states.
		// Track if we found an accepted state that has a null transition and
		// all Characters that fail to match.
		boolean acceptedNullTransition = false;
		Set<Character> excludedCharacters = new HashSet<Character>();
		for (_State state : transitions.keySet()) {
			// Initializing bookkeeping variables
			Set<_State> visitedStates = new HashSet<_State>();
			Set<_State> fringeStates = new HashSet<_State>();
			fringeStates.add(state);
			visitedStates.add(state);
			boolean found = false;

			while (fringeStates.isEmpty() == false) {
				// Get the next state and remove it from the fringe.
				_State fringe = fringeStates.iterator().next();
				fringeStates.remove(fringe);

				// Add this state to the visited set
				visitedStates.add(fringe);

				// Check terminal condition
				if (this.finalState.equals(fringe)) {
					// If the state we started searching from has a null
					// transition, mark a flag
					if (nullStates.contains(state)) {
						acceptedNullTransition = true;
					} else {
						includedCharacters.addAll(transitions.get(state));
					}
					found = true;
					break;
				}

				// Add to the fringe set
				for (_State newFringeState : fringe.getDestinations()) {
					if (visitedStates.contains(newFringeState) == false) {
						fringeStates.add(newFringeState);
					}
				}
			}

			// If we cannot hit an accept state from the original destination, 
			// add its characters to the excludedCharacters list.
			if (found == false) {
				excludedCharacters.addAll(transitions.get(state));
			}
		}

		// If the we found a state that can be accepted that is transitioned
		// into by a wildcard, we add everything to the set of accepted
		// characters, remove the excluded characters, and then re-add the
		// characters that we found to be able to hit the accept state.
		// TODO: Change to a different alphabet
		if (acceptedNullTransition) {
			Set<Character> result =
					RegExAlphabet.DEFAULT_ALPHABET.getAllCharacters();
			result.removeAll(excludedCharacters);
			result.addAll(includedCharacters);
			return result;
		}

		// Otherwise, we just return the included characters we found.
		return includedCharacters;
	}

	/**
	 * Converts this state machine into a DFA.
	 */
	public void convertToDFA() {
		// TODO: Fix up equality and hashing of _State and Set<_State> objects
//		LogUtilities.logln("NFA: ");
//		LogUtilities.logln(this);
		
		// Construct the first table
		// First initialize our table and epsilon column
		Map<_State, Map<Character, Set<_State>>> table1 =
				new HashMap<_State, Map<Character,Set<_State>>>();
		Map<_State, Set<_State>> table1Epsilons =
				new HashMap<_State, Set<_State>>();
		
		// Iterate over all states
		for (_State state : this.states) {
			// Record all transitions
			table1.put(state, new HashMap<Character, Set<_State>>());
			for (Character c : state.charToStateSetMap.keySet()) {
				table1.get(state).put(c, state.charToStateSetMap.get(c));
			}
			
			// Record all epsilon equivalences
			table1Epsilons.put(state, state.getEpsilonStates());
		}
		
		// Now we construct second table.
		// First initialize a bunch of bookkeeping variables
		Map<Set<_State>, Map<Character, Set<_State>>> table2 =
				new HashMap<Set<_State>, Map<Character,Set<_State>>>();
		Set<Set<_State>> processedSets = new HashSet<Set<_State>>();
		Set<Set<_State>> unprocessedSets = new HashSet<Set<_State>>();
		Set<_State> initialSet = new HashSet<_State>();
		initialSet.add(this.initialState);
		initialSet.addAll(table1Epsilons.get(this.initialState));
		unprocessedSets.add(initialSet);
		
		// Iterate while there are still sets to process 
		while (unprocessedSets.isEmpty() == false) {
//			LogUtilities.logln("unprocessedStates: {");
//			ArrayList<Set<_State>> unprocessedStates = new ArrayList<Set<_State>>();
//			unprocessedStates.addAll(unprocessedSets);
//			for (Set<_State> stateSet : unprocessedStates) {
//				ArrayList<_State> stateSetList = new ArrayList<_State>();
//				stateSetList.addAll(stateSet);
//				LogUtilities.log("\t[ ");
//				for (_State state : stateSetList) {
//					LogUtilities.log(state.id + " ");
//				}
//				LogUtilities.logln("]");
//			}
//			LogUtilities.logln("}");
//			
//			LogUtilities.logln("processedStates: {");
//			ArrayList<Set<_State>> processedStates = new ArrayList<Set<_State>>();
//			processedStates.addAll(processedSets);
//			for (Set<_State> stateSet : processedStates) {
//				ArrayList<_State> stateSetList = new ArrayList<_State>();
//				stateSetList.addAll(stateSet);
//				LogUtilities.log("\t[ ");
//				for (_State state : stateSetList) {
//					LogUtilities.log(state.id + " ");
//				}
//				LogUtilities.logln("]");
//			}
//			LogUtilities.logln("}");
			
			// Pull off a set from the set of unprocessed sets
			Set<_State> currentSet = unprocessedSets.iterator().next();
			unprocessedSets.remove(currentSet);
			processedSets.add(currentSet);
			
			
//			LogUtilities.log("currentSet: [");
//			ArrayList<_State> currentStates = new ArrayList<_State>(currentSet);
//			for (_State state : currentStates) {
//				LogUtilities.log(" " + state.id);
//			}
//			LogUtilities.logln(" ]\n");
			
			// Initialize and extract the involved row of the table
			table2.put(currentSet, new HashMap<Character, Set<_State>>());
			Map<Character, Set<_State>> currentRow = table2.get(currentSet);
			
			// Collect all involved columns of the table
			Set<Character> transitions = new HashSet<Character>();
			for (_State startState : currentSet) {
				transitions.addAll(table1.get(startState).keySet());
			}
			
			// Iterate over each of those columns
			for (Character transition : transitions) {
				// Initialize and extract the contents of a cell of our table
				currentRow.put(transition, new HashSet<_State>());
				Set<_State> followingStates = currentRow.get(transition);
				
				// Iterate over each of the current states, adding states to the
				// current cell of our table
				for (_State startingState : currentSet) {
					// Add their direct transitions
					Set<_State> destinations =
							table1.get(startingState).get(transition);
					
					// If this transition doesn't exist, move on
					if (destinations == null) {
						continue;
					}
					
					followingStates.addAll(destinations);
					
					// Account for one or more epsilon transitions
					for (_State destination : destinations) {
						followingStates.addAll(table1Epsilons.get(destination));
					}
				}
				
				// If the set of states in this cell of our table has already
				// been processed or is about to be processed, continue
				if (processedSets.contains(followingStates) ||
						unprocessedSets.contains(followingStates)) {
					continue;
				}
				// Otherwise add it to our set of unprocessed sets
				unprocessedSets.add(followingStates);
			}
		}
		
		// Construct a new state for each set of former states
		Map<Set<_State>, _State> oldToNewMap =
				new HashMap<Set<_State>, _State>();
		Set<_State> newStateSet = new HashSet<_State>();
		for (Set<_State> set : processedSets) {
			oldToNewMap.put(set, new _State(oldToNewMap.size()));
			newStateSet.add(oldToNewMap.get(set));
		}
		
		// Hydrate the new states with the mappings of the sets of old states
		for (Set<_State> set : oldToNewMap.keySet()) {
			_State newState = oldToNewMap.get(set);
			
			// Iterate over all columns of table2
			for (Character c : table2.get(set).keySet()) {
				// Get the destination set of old states
				Set<_State> dest = table2.get(set).get(c);
				
				// Get the corresponding new state
				_State newDest = oldToNewMap.get(dest);
				
				// Create the transition
				newState.addTransition(c, newDest);
			}
		}
		
		// Get the initial state
		_State newInitialState = oldToNewMap.get(initialSet);
		
		// Get the new final state(s)
		Set<_State> newFinalStates = new HashSet<_State>();
		for (Set<_State> oldStateSet : oldToNewMap.keySet()) {
			if (oldStateSet.contains(this.finalState)) {
				newFinalStates.add(oldToNewMap.get(oldStateSet));
			}
		}
		
		// If necessary, reduce the final states to one state
		_State newFinalState = null;
		if (newFinalStates.size() == 1) {
			newFinalState = newFinalStates.iterator().next();
		} else {
			newFinalState = new _State(oldToNewMap.size());
			for (_State state : newFinalStates) {
				state.epsilonTransitions.add(newFinalState);
			}
			newStateSet.add(newFinalState);
		}
		
		// Get the states list
		List<_State> newStates = new ArrayList<_State>();
		for (_State newState : newStateSet) {
			newStates.add(newState);
		}
		
		// Update this state machine
		this.initialState = newInitialState;
		this.finalState = newFinalState;
		this.states = newStates;
		
//		LogUtilities.logln("DFA: ");
//		LogUtilities.logln(this);
	}
}
