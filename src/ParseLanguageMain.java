import immutable.Grammar;
import immutable.Language;
import immutable.Report;
import utilities.FileUtilities;
import utilities.LogUtilities;
import generators.GrammarGenerator;
import generators.LanguageGenerator;
import generators.ReportGenerator;
import grammar.defaults.RegExGrammar;

/**
 * Just contains the Main method and console output constants and methods.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class ParseLanguageMain {
	
	public static void main(String[] args) throws Exception {
		String langSpecFile = args[0];
		String inputFile = args[1];
		
		if (args.length == 3 && args[2].equals("-v")) {
			LogUtilities.activate();
		}
		
        // Get the default regular expression grammar
        Grammar regExGrammar = RegExGrammar.instance;
        
        // Generate language specification report
        Report langSpecReport = ReportGenerator.generate(
                        regExGrammar,
                        FileUtilities.getFileContents(langSpecFile));

        LogUtilities.logln(langSpecReport);

        // Generate NFA state machines
        Language language = LanguageGenerator.generate(langSpecReport);
        LogUtilities.logln(language);

        // Generate the Grammar from that Language
        Grammar grammar = GrammarGenerator.generate(language);
        
        // Generate token report
        Report report = ReportGenerator.generate(
                        grammar,
                        FileUtilities.getFileContents(inputFile));
        
        // Print the report
        System.out.println(report);
	}
}
