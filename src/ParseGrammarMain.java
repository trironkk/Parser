import immutable.Grammar;
import immutable.Report;
import utilities.FileUtilities;
import utilities.LogUtilities;
import generators.GrammarGenerator;
import generators.ReportGenerator;

/**
 * Just contains the Main method and console output constants and methods.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class ParseGrammarMain {
	public static void main(String[] args) throws Exception {
		
		String tokenSpecFile = args[0];
		String grammarSpecFile = args[1];
		String inputFile = args[2];

		if (args.length == 4 && args[3].equals("-v")) {
			LogUtilities.activate();
		}
		
		Grammar generatedGrammar = GrammarGenerator.generate(
				FileUtilities.getFileContents(grammarSpecFile),
				FileUtilities.getFileContents(tokenSpecFile));

//		Grammar generatedGrammar = GrammarGenerator.generate(
//				grammar,
//				FileUtilities.getFileContents(grammarTokenSpecFile));
		
		LogUtilities.logln(generatedGrammar);

		Report inputFileReport = ReportGenerator.generate(
				generatedGrammar,
				FileUtilities.getFileContents(inputFile));
		
		System.out.println(inputFileReport);
	}
}
