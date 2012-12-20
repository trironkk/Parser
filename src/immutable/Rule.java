package immutable;

import java.util.ArrayList;
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
public class Rule {
	
	/**
	 * The reader-friendly name of this state.
	 */
	public final String name;
	
	/**
	 * A list of linked lists that represent possible child transitions of this
	 * State.
	 */
	public final List<ArrayList<Object>> possibleChildren;
	
	/**
	 * This is a set of all token types that this state can match.
	 */
	public final Set<TokenType> startingTokenTypes;
	
	/**
	 * Standard constructor.
	 */
	public Rule(String name, List<ArrayList<Object>> possibleChildren,
			Set<TokenType> startingTokenTypes) {
		this.name = name;
		this.possibleChildren = possibleChildren;
		this.startingTokenTypes = startingTokenTypes;
	}
	
	public String toString() {
		return this.name;
	}
}