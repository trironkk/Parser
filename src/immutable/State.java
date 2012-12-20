package immutable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utilities.ErrorUtilities;

/**
 * This is an immutable information holder class.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class State {
	
	/**
	 * This string serves as a reader-friendly name. The naming convention is
	 * "State" followed by the State's identifier (e.g. State1, State2, State3). 
	 */
	public final String name;
	
	/**
	 * This Map enables performant traversal of a Table, enabling retrieval
	 * of destination states in constant time.
	 */
	public final Map<Character, Set<State>> charToStateSetMap;
	
	/**
	 * This Map enables performant generation of reader-friendly string
	 * representations of this State.
	 */
	public final Map<State, Set<Character>> stateToCharSetMap;
	
	/**
	 * This Set contains all states to which this state can epsilon transition
	 * into.
	 */
	public final Set<State> epsilonTransitions;
	
	/**
	 * Standard constructor.
	 */
	public State(String name,
			Map<Character, Set<State>> charToStateSetMap,
			Map<State, Set<Character>> stateToCharSetMap,
			Set<State> epsilonTransitions) {
		this.name = name;
		this.charToStateSetMap = charToStateSetMap;
		this.stateToCharSetMap = stateToCharSetMap;
		this.epsilonTransitions = epsilonTransitions;
	}
	
	/**
	 * This method returns the State that a character would transition into, or
	 * null if there is no defined map destination state.
	 * 
	 * Note: If a particular Character doesn't have a State that it maps to,
	 * we check null.
	 */
	public Set<State> getDestinations(Character c) {
		Set<State> result = new HashSet<State>();
		
		// Aggregate all State objects that this State can directly transition
		// into with the specified character.
		if (this.charToStateSetMap.containsKey(c)) {
			result.addAll(this.charToStateSetMap.get(c));
		} else if (this.charToStateSetMap.containsKey(null)) {
			result.addAll(this.charToStateSetMap.get(null));
		}
		
		// Include all State objects that any epsilon transition states can
		// transition into as well.
		for (State equivalentState : this.epsilonTransitions) {
			result.addAll(equivalentState.getDestinations(c));
		}
		
		return result;
	}
	
	/**
	 * This method handles the bookkeeping for the insertion of a transition.
	 */
	public void addTransition(Character c, State destination) {

		// Duplicate checks
		if (stateToCharSetMap.containsKey(destination) &&
				stateToCharSetMap.get(destination).contains(c))
		{
			//			ErrorUtilities.throwError(c + " already exists in " +
			//					destination.name + ".");
			return;
		}

		if (charToStateSetMap.containsKey(c) == false) {
			HashSet<State> emptySet = new HashSet<State>();
			charToStateSetMap.put(c, emptySet);
		} else {
			ErrorUtilities.throwError(c + " already exists.");
		}
		charToStateSetMap.get(c).add(destination);

		if (stateToCharSetMap.containsKey(destination) == false) {
			HashSet<Character> emptySet = new HashSet<Character>();
			stateToCharSetMap.put(destination, emptySet);
		}
		stateToCharSetMap.get(destination).add(c);
	}

	/**
	 * This method allows for the mapping of a set of characters to a
	 * destination.
	 */
	public void addTransitions(Set<Character> characterSet, State destination) {
		for (Character c : characterSet) {
			addTransition(c, destination);
		}
	}
	
	/**
	 * This method returns the State that a character would transition into, or
	 * null if there is no defined map destination state.
	 */
	public Set<State> getDestinations() {
		Set<State> result = new HashSet<State>();
		
		for (State s : stateToCharSetMap.keySet()) {
			result.add(s);
		}
		
		// Include all State objects that any epsilon transition states can
		// transition into as well.
		for (State equivalentState : this.epsilonTransitions) {
			result.addAll(equivalentState.getDestinations());
		}
		
		return result;
	}
	
	/**
	 * Overriding the default toString().
	 */
	public String toString() {
		String result = new String();
		
		result += name + '\n';
		
		for (State s : stateToCharSetMap.keySet()) {
			result += "  " + s.name + ": " + this.stateToCharSetMap.get(s) + "\n"; 
		}
		result = (String) result.subSequence(0, result.length() - 1);
		if (this.epsilonTransitions.isEmpty() == false) {
			result += "\n  Epsilon Transitions: ";
			for (State epsilonTransition : this.epsilonTransitions) {
				result += epsilonTransition.name + ' ';
			}
		}
		if (stateToCharSetMap.keySet().isEmpty() &&
				this.epsilonTransitions.isEmpty()) {
			result += "\n  [None]";
		}
		
		return result;
	}
	
	/**
	 * Overriding equals functionality.
	 */
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != this.getClass())
			return false;
		
		State rhs = (State)o;
		return (this.name == rhs.name);
	}
	
	/**
	 * Overriding hashcode functionality.
	 */
	public int hashCode() {
		int hashcode = this.name.hashCode();
		return hashcode;
	}
}
