package immutable;

import java.util.List;
import java.util.Set;

import utilities.StringUtilities;

/**
 * Associates an initial, final state, a list of involved states, and the
 * related Token for a particular component
 * 
 * @author Trironk Kiatkungwanglai
 */
public class StateMachine {

	/**
	 * The initial state.
	 */
	public final State initialState;
	
	/**
	 * The final state.
	 */
	public final State finalState;
	
	/**
	 * A list of involved states.
	 */
	public final List<State> states;
	
	/**
	 * This is a set of all Characters that this state can match.
	 */
	public final Set<Character> startingCharacters;
	
	/**
	 * Standard constructor.
	 */
	public StateMachine(State initialState, State finalState,
			List<State> states, Set<Character> startingCharacters) {
		this.initialState = initialState;
		this.finalState = finalState;
		this.states = states;
		this.startingCharacters = startingCharacters;
	}
	
	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		String result = new String();
		result += "Initial State: " + this.initialState.name;
		result += "\nFinal State: " + this.finalState.name;
		result += "\nStates:\n";
		for (State s : this.states) {
			result += StringUtilities.tabify(s.toString());
			result += '\n';
		}
		
		return result;
	}
}
