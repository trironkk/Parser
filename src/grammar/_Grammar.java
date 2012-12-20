package grammar;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a set of rules that dictate the parsing of a language.
 *
 * @author Trironk Kiatkungwanglai
 */
public class _Grammar {

	/**
	 * The root node.
	 */
	public _Rule root;
	
	/**
	 * A set of _Rule objects.
	 */
	public Set<_Rule> rules;
	
	/**
	 * The name of this Grammar.
	 */
	public String name;
	
	/**
	 * Default constructor.
	 */
	public _Grammar(String name) {
		this.name = name;
		this.rules = new HashSet<_Rule>();
	}
}
