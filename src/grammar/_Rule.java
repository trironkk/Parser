package grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is an information holder class, which represents a node of a recursive
 * descent tree. The tree, however, doesn't get initialized - instead, it's
 * built as it is explored. Each node contains a list of objects, some of which
 * are other nodes, and others are other State objects. The bookkeeping for that
 * list is handled in this class.
 *
 * @author Trironk Kiatkungwanglai
 */
public class _Rule {
	
	/**
	 * The reader-friendly name of this state.
	 */
	public String name;
	
	/**
	 * A list of linked lists that represent possible child transitions of this
	 * State.
	 */
	public List<ArrayList<Object>> possibleChildren;
	
	/**
	 * This is a set of all Characters that this state can match.
	 */
	public Set<Character> startingCharacters;
	
	/**
	 * A simple constructor.
	 */
	public _Rule(String name) {
		this.name = name;
		this.possibleChildren = new ArrayList<ArrayList<Object>>();
		this.startingCharacters = new HashSet<Character>();
	}

	/**
	 * Adds a new List to possibleChildren.
	 */
	public void addNewList() {
		possibleChildren.add(new ArrayList<Object>());
	}
	
	/**
	 * Adds an item to the last list in possibleChildren.
	 */
	public void addToTail(Object item) {
		possibleChildren.get(possibleChildren.size() - 1).add(item);
	}
}