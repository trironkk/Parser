package generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import language.defaults.RegExLanguage;
import immutable.Token;
import immutable.Report;
import utilities.ErrorUtilities;
import utilities.FileUtilities;
import immutable.Grammar;
import immutable.Language;
import immutable.TokenType;
import grammar._Rule;
import grammar._Grammar;
import grammar.defaults.RegExGrammar;
import grammar.defaults.SimpleGrammar;

public class GrammarGenerator {
	
	public static final String SPEC_FILE =
			"resources/token_spec";
	
	public static final String TOKEN_SPEC =
			FileUtilities.getStreamContents(
					GrammarGenerator.class.getResourceAsStream(
							GrammarGenerator.SPEC_FILE));
	
	/** 
	 * Generates a simple grammar that will produce a list of tokens.
	 */
	public static Grammar generate(Language language) {
		return SimpleGrammar.createSimpleGrammar(language);
	}
	
	/**
	 * Generates a grammar from a specification. In order
	 */
	public static Grammar generate(String grammarSpec,
			String grammarTokenSpec) {
		
		// Initialize some instance variables
		Grammar regExGrammar = RegExGrammar.instance;
		
		// Load the language that specifies the token types legal for grammar
		// specification files
        Report tokenSpecReport = ReportGenerator.generate(
                regExGrammar,
                TOKEN_SPEC);

		Language tokenSpecLanguage =
        		LanguageGenerator.generate(tokenSpecReport);
        
        Grammar tokenSpecGrammar = GrammarGenerator.generate(tokenSpecLanguage);

        // Parse the grammar token specification file for tokens that are valid
        // in this grammar and construct a new Language from it.
        Report grammarTokenSpecReport = ReportGenerator.generate(
        		regExGrammar,
        		grammarTokenSpec);

        Language grammarTokenLanguage =
        		LanguageGenerator.generate(grammarTokenSpecReport);
        
        // Parse the grammar specification file for grammar structure and
        // construct a new grammar
        Report grammarSpecReport = ReportGenerator.generate(
        		tokenSpecGrammar,
        		grammarSpec);
        
        // Split the report by NEW_LINE tokens
        List<List<Token>> grammarReportLines = new ArrayList<List<Token>>();
        grammarReportLines.add(new ArrayList<Token>());
        List<Token> lastLine =
        		grammarReportLines.get(grammarReportLines.size() - 1);
        for (Token t : grammarSpecReport.tokens) {
			if (t.tokenType == RegExLanguage.NEW_LINE) {
				if (lastLine.size() > 0) {
					grammarReportLines.add(new ArrayList<Token>());
					lastLine =
							grammarReportLines.get(grammarReportLines.size() - 1);
				}
			} else {
				lastLine.add(t);
			}
		}
        if (lastLine.size() == 0) {
        	grammarReportLines.remove(grammarReportLines.size() - 1);
        }
        
        // Extract the name of the root rule
        String rootRuleName = grammarReportLines.get(0).get(0).contents;
        
        // Construct a map of rule name to the tokens that represent the
        // children of that rule.
        Map<String, List<List<Token>>> rulesTokens =
        		new HashMap<String, List<List<Token>>>();
        for (List<Token> tokenList : grammarReportLines) {
        	String name = tokenList.get(0).contents;
			if (rulesTokens.containsKey(name) == false) {
				rulesTokens.put(name, new ArrayList<List<Token>>());
			}
			
			List<Token> chain = new ArrayList<Token>();
			// Start on index 2 here to skip the RULE and RULE_ASSIGN tokens.
			for (int i = 2; i < tokenList.size(); i++) {
				chain.add(tokenList.get(i));
			}
			rulesTokens.get(name).add(chain);
		}
        
        // Construct a map that has the OR tokens removed
        Map<String, List<List<Token>>> temp =
        		new HashMap<String, List<List<Token>>>();
        for (String name: rulesTokens.keySet()) {
        	List<List<Token>> newChains = new ArrayList<List<Token>>();
        	temp.put(name, newChains);
        	
        	List<List<Token>> chains = rulesTokens.get(name);
        	for (List<Token> chain : chains) {
        		List<Token> lastChain = new ArrayList<Token>();
        		newChains.add(lastChain);
				for (Token token : chain) {
					// This seems like a hack, but since the token is defined at
					// runtime, this is the best we can do.
					if (token.contents.equals("|")) {
						lastChain = new ArrayList<Token>();
						newChains.add(lastChain);
					} else {
						lastChain.add(token);
					}
				}
			}
		}
        rulesTokens = temp;
        
        // Initialize the rules map
        Map<String, _Rule> rules = new HashMap<String, _Rule>();
        for (String name : rulesTokens.keySet()) {
        	rules.put(name, new _Rule(name));
		}
        
        // Hydrate the rules map according to token chains
        for (String name : rulesTokens.keySet()) {
        	// Get the current rule
        	_Rule currentRule = rules.get(name);
        	
        	// Iterate over all the chains, hydrating objects according to their
        	// token's first starting character. This seems like a hack, but
        	// since the token is defined at runtime, this is the best we can
        	// do.
        	List<List<Token>> chains = rulesTokens.get(name);
        	for (List<Token> chain : chains) {
        		currentRule.addNewList();
				for (Token token : chain) {
					String contents = token.contents;
					if (contents.charAt(0) == '<') {
						// Get a reference to the rule from the ruleTokens map
						// and add a reference to it to this chain.
						_Rule rule = rules.get(contents);
						if (rule == null) {
							String msg = name + " references the rule " +
									contents + ", which was not specified in " +
									"the grammar specification file.";
							ErrorUtilities.throwError(msg);
						}
						currentRule.addToTail(rule);
					} else if (contents.charAt(0) == '$') {
						// Get the token type as defined by the grammar token
						// language and add a reference to it to this chain.
						TokenType tokenType =
								grammarTokenLanguage.tokenTypes.get(contents);
						if (tokenType == null) {
							String msg = name + " references the token type " +
									contents + ", which was not specified in " +
									"the grammar token specification file.";
							ErrorUtilities.throwError(msg);
						}
						currentRule.addToTail(tokenType);
					} else {
						ErrorUtilities.throwError("Unrecognized token type.");
					}
				}
			}
		}
        
        _Grammar generatedGrammar = new _Grammar("Generated Grammar");
        generatedGrammar.rules = new HashSet<_Rule>(rules.values());
        generatedGrammar.root = rules.get(rootRuleName);

        return new Grammar(generatedGrammar);
	}
}
