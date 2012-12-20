package grammar.defaults;

import immutable.Grammar;
import immutable.Language;
import immutable.TokenType;

import java.util.HashSet;

import language.defaults.RegExLanguage;
import grammar._Grammar;
import grammar._Rule;

/**
 * This class produces context free Grammar objects based on language
 * specifications.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class SimpleGrammar {
	
	/**
	 * Constructs a Grammar object that will always match all tokens from a
	 * given Language.
	 */
	public static Grammar createSimpleGrammar(Language language) {
		// Construct a Rule
		_Rule mutableRule = new _Rule("root");
		mutableRule.addNewList();
		mutableRule.addToTail(RegExLanguage.NEW_LINE);
		mutableRule.addToTail(mutableRule);
		mutableRule.addNewList();
		mutableRule.addToTail(RegExLanguage.EPSILON);
		mutableRule.addToTail(mutableRule);
		for (TokenType tokenType : language.tokenTypes.values()) {
			mutableRule.addNewList();
			mutableRule.addToTail(tokenType);
			mutableRule.addToTail(mutableRule);
		}
		
		_Grammar mutableGrammar = new _Grammar("ContextFreeGrammar");
		mutableGrammar.root = mutableRule;
		mutableGrammar.rules = new HashSet<_Rule>();
		mutableGrammar.rules.add(mutableGrammar.root);
		
		return new Grammar(mutableGrammar);
	}
}
