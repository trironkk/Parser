package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import language._State;
import language._StateMachine;

/**
 * This class contains static methods that simplify copying networks of objects.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class CopyUtilities {
	
	public static Map<_State, _State> CopyStates(List<_State> oldStates) {
		// Construct a mapping of old State object to new State objects
		Map<_State, _State> result = new HashMap<_State, _State>();
		for (_State oldState : oldStates) {
			result.put(oldState, new _State(oldState.id));
		}
		
		for (_State oldState : oldStates) {
			// Hydrate transitions
			_State newState = result.get(oldState);
			for (Character c : oldState.charToStateSetMap.keySet()) {
				for (_State dest : oldState.charToStateSetMap.get(c)) {
					newState.addTransition(c, result.get(dest));
				}
			}
			
			for (_State epsilonState : oldState.epsilonTransitions) {
				newState.epsilonTransitions.add(result.get(epsilonState));
			}			
		}
		
		return result;
	}
	
	public static _StateMachine CopyStateMachine(_StateMachine stateMachine) {
		// Map out all the states
		Map<_State, _State> stateMap = CopyStates(stateMachine.states);
		
		// Extract initial _State object
		_State initialState = stateMap.get(stateMachine.initialState);
		
		// Extract final _State objects 
		_State finalState = stateMap.get(stateMachine.finalState);
		
		// Reconstruct the list of state equivalents
		List<_State> newStates = new ArrayList<_State>();
		for (_State oldState : stateMachine.states) {
			newStates.add(stateMap.get(oldState));
		}
		
		_StateMachine result = new _StateMachine(initialState, finalState);
		for (_State s : newStates) {
			result.states.add(s);
		}
		return result;
	}
}
