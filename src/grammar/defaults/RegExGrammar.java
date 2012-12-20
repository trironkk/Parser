package grammar.defaults;

import java.util.HashSet;
import java.util.Set;

import immutable.Grammar;
import grammar._Grammar;
import grammar._Rule;

import language.defaults.*;

/**
 * This class represents a default Grammar, to be used in the parsing of the
 * regular expression that defines the
 * 
 * @author Trironk Kiatkungwanglai
 */
public class RegExGrammar {

	/**
	 * This is the only instance of RegExGrammar, and it is made available with
	 * the singleton pattern.
	 */
	public static final Grammar instance;
	
	/**
	 * Constructs a new RegExGrammar object, which is a pre-built Grammar object
	 * that can handle defines the regular expression grammar.
	 */
	static {

		// Construct the State objects
		_Rule regex = new _Rule("<reg-ex>");
		_Rule rexp = new _Rule("<rexp>");
		_Rule rexpa = new _Rule("<rexpa>");
		_Rule rexp1 = new _Rule("<rexp1>");
		_Rule rexp1a = new _Rule("<rexp1a>");
		_Rule rexp2 = new _Rule("<rexp2>");
		_Rule rexp2_tail = new _Rule("<rexp2_tail>");
		_Rule rexp3 = new _Rule("<rexp3>");
		_Rule char_class = new _Rule("<char_class>");
		_Rule char_class1 = new _Rule("<char_class1>");
		_Rule char_set_list = new _Rule("<char_set_list>");
		_Rule char_set = new _Rule("<char_set>");
		_Rule char_set_tail = new _Rule("<char_set_tail>");
		_Rule exclude_set = new _Rule("<exclude_set>");
		_Rule exclude_set_tail = new _Rule("<exclude_set_tail>");
		
		// Construct a list of _Rule objects
		Set<_Rule> rules = new HashSet<_Rule>();
		rules.add(regex);
		rules.add(rexp);
		rules.add(rexpa);
		rules.add(rexp1);
		rules.add(rexp1a);
		rules.add(rexp2);
		rules.add(rexp2_tail);
		rules.add(rexp3);
		rules.add(char_class);
		rules.add(char_class1);
		rules.add(char_set_list);
		rules.add(char_set);
		rules.add(char_set_tail);
		rules.add(exclude_set);
		rules.add(exclude_set_tail);

		// Define the properties of each of the State objects
		regex.addNewList();
		regex.addToTail(rexp);

		rexp.addNewList();
		rexp.addToTail(rexp1);
		rexp.addToTail(rexpa);
		
		rexpa.addNewList();
		rexpa.addToTail(RegExLanguage.UNION);
		rexpa.addToTail(rexp1);
		rexpa.addToTail(rexpa);
		rexpa.addNewList();
		rexpa.addToTail(RegExLanguage.EPSILON);
		
		rexp1.addNewList();
		rexp1.addToTail(rexp2);
		rexp1.addToTail(rexp1a);
		
		rexp1a.addNewList();
		rexp1a.addToTail(rexp2);
		rexp1a.addToTail(rexp1a);
		// START DEVIATION FROM GIVEN SPEC
		rexp1a.addNewList();
		rexp1a.addToTail(RegExLanguage.NEW_LINE);
		rexp1a.addToTail(rexp1a);
		// END DEVIATION FROM GIVEN SPEC
		rexp1a.addNewList();
		rexp1a.addToTail(RegExLanguage.EPSILON);
		
		rexp2.addNewList();
		rexp2.addToTail(RegExLanguage.OPEN_PARENS);
		rexp2.addToTail(rexp);
		rexp2.addToTail(RegExLanguage.CLOSE_PARENS);
		rexp2.addToTail(rexp2_tail);
		rexp2.addNewList();
		rexp2.addToTail(RegExLanguage.RE_CHAR);
		rexp2.addToTail(rexp2_tail);
		rexp2.addNewList();
		rexp2.addToTail(rexp3);
		
		rexp2_tail.addNewList();
		rexp2_tail.addToTail(RegExLanguage.STAR);
		rexp2_tail.addNewList();
		rexp2_tail.addToTail(RegExLanguage.PLUS);
		rexp2_tail.addNewList();
		rexp2_tail.addToTail(RegExLanguage.EPSILON);
		
		rexp3.addNewList();
		rexp3.addToTail(char_class);
		rexp3.addNewList();
		rexp3.addToTail(RegExLanguage.EPSILON);

		char_class.addNewList();
		char_class.addToTail(RegExLanguage.DOT);
		char_class.addNewList();
		char_class.addToTail(RegExLanguage.OPEN_BRACKET);
		char_class.addToTail(char_class1);
		char_class.addNewList();
		char_class.addToTail(RegExLanguage.DEFINED_CLASS);
		
		char_class1.addNewList();
		char_class1.addToTail(char_set_list);
		char_class1.addNewList();
		char_class1.addToTail(exclude_set);
		
		char_set_list.addNewList();
		char_set_list.addToTail(char_set);
		char_set_list.addToTail(char_set_list);
		char_set_list.addNewList();
		char_set_list.addToTail(RegExLanguage.CLOSE_BRACKET);
		
		char_set.addNewList();
		char_set.addToTail(RegExLanguage.CLS_CHAR);
		char_set.addToTail(char_set_tail);
		
		char_set_tail.addNewList();
		char_set_tail.addToTail(RegExLanguage.DASH);
		char_set_tail.addToTail(RegExLanguage.CLS_CHAR);
		char_set_tail.addNewList();
		char_set_tail.addToTail(RegExLanguage.EPSILON);
		
		exclude_set.addNewList();
		exclude_set.addToTail(RegExLanguage.CARROT);
		exclude_set.addToTail(char_set);
		exclude_set.addToTail(RegExLanguage.CLOSE_BRACKET);
		exclude_set.addToTail(RegExLanguage.IN);
		exclude_set.addToTail(exclude_set_tail);
		
		exclude_set_tail.addNewList();
		exclude_set_tail.addToTail(RegExLanguage.OPEN_BRACKET);
		exclude_set_tail.addToTail(char_set);
		exclude_set_tail.addToTail(RegExLanguage.CLOSE_BRACKET);
		exclude_set_tail.addNewList();
		exclude_set_tail.addToTail(RegExLanguage.DEFINED_CLASS);
		
		// Construct this Grammar
		_Grammar mutableGrammar = new _Grammar("RegExGrammar");
		mutableGrammar.root = regex;
		mutableGrammar.rules = rules;
		
		// Save a locked copy of this grammar
		instance = new Grammar(mutableGrammar);
	}
}
