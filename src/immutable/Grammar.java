package immutable;

import grammar._Grammar;
import grammar._Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utilities.ConversionUtilities;

/**
 * This class represents a set of rules that dictate the parsing of a language.
 *
 * @author Trironk Kiatkungwanglai
 */
public class Grammar {

	/**
	 * The name of this Grammar.
	 */
	public final String name;
	
	/**
	 * The root node.
	 */
	public final Rule root;
	
	/**
	 * A list of rule objects.
	 */
	public final Set<Rule> rules;
	
	/**
	 * Standard constructor.
	 */
	public Grammar(_Grammar mutableGrammar) {
		// Get a mapping from _Rule objects to their Rule object counterparts
		Map<_Rule, Rule> ruleMap =
				ConversionUtilities.convertRules(mutableGrammar.rules); 
		Set<Rule> rules = new HashSet<Rule>();
		rules.addAll(ruleMap.values());
		
		// Assign final fields
		this.name = mutableGrammar.name;
		this.root = ruleMap.get(mutableGrammar.root);
		this.rules = rules;
		
		// Compute all starting characters
		Map<Rule, Set<TokenType>> startingTokenTypes = 
				new HashMap<Rule, Set<TokenType>>();
		for (Rule rule : this.rules) {
			computeStartingTokenTypes(rule, startingTokenTypes);
		}
	}

	/**
	 * Compute the starting characters of all of the rules and save the results
	 * in the given startingCharacters.
	 */
	private void computeStartingTokenTypes(Rule rule,
			Map<Rule, Set<TokenType>> startingTokenTypes) {
		// If this has already been computed, don't recompute it.
		if (startingTokenTypes.containsKey(rule)) {
			return;
		}
		
		// Initialize the list of starting characters
		startingTokenTypes.put(rule, new HashSet<TokenType>());
		
		// Iterate over all possible lists
		for (List<Object> chain : rule.possibleChildren) { 
			
			// If the first character is a TokenType, just add it.
			if (TokenType.class ==  chain.get(0).getClass()) {
				TokenType firstItem = (TokenType)chain.get(0); 
				startingTokenTypes.get(rule).add(firstItem);
			}
			
			// If the first character is a Rule, compute and add its starting
			// token types.
			if (Rule.class == chain.get(0).getClass()) {
				Rule childRule = (Rule)chain.get(0);

				// Recursively call computeStartingCharacters on it if necessary
				if (startingTokenTypes.containsKey(childRule) == false) {
					computeStartingTokenTypes(childRule, startingTokenTypes);
				}
				
				// Add all characters that this rule could transition into to
				// the set of starting characters
				startingTokenTypes.get(rule).addAll(
						startingTokenTypes.get(childRule));
			}
		}
		rule.startingTokenTypes.addAll(startingTokenTypes.get(rule));
	}
	
	public String toString() {
		String result = this.name;
		result += "\nRoot Rule: " + root;
		for (Rule rule : this.rules) {
			result += "\n  " + rule.name;
			for (List<Object> chain : rule.possibleChildren) {
				result += "\n    [ ";
				for (Object item : chain) {
					result += item + " ";
				}
				result += "]";
			}
		}
		return result;
	}
}
