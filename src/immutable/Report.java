package immutable;

import immutable.Grammar;

import java.util.List;

import utilities.StringUtilities;
/**
 * This is an information holder class, containing all information about a
 * document parsed with a given Grammar. This class should only serve to handle
 * the bookkeeping - all token parsing logic should reside in TokenParser.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class Report
{
	/**
	 * This is a list of all the tokens that were extracted from the document.
	 */
	public final List<Token> tokens;

	/**
	 * This is the Grammar object that will be used to parse the document.
	 */
	public final Grammar grammar;
	
	/**
	 * This is an empty constructor, serving only to initialize the fields of
	 * this class.
	 */
	public Report(Grammar grammar, List<Token> tokens) {
		this.grammar = grammar;
		this.tokens = tokens;
	}
	
	/**
	 * Returns a String representation of this object.
	 */
	public String toString() {
		String result = new String();
		
		// TODO: Extract formatting constants into public static final variables
		// TODO: Intelligently determine the appropriate size of the TokenType
		// name.
		
		// Get the width of the index
		int indexWidth = 2;
		int temp = tokens.size();
		while (temp > 0) {
			temp /= 10;
			indexWidth++;
		}
		
		int count = 1;
		for (Token token : tokens) {
			result += StringUtilities.padRight("" + count + ":", indexWidth) + 
					StringUtilities.padRight(token.toString(), 35);

			// TODO: Add in a way of printing columns
			count++;
			result += '\n';
		}
		
		return result;
	}
}
