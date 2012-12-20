package utilities;

/**
 * This class contains static methods that simplify common String operations.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class StringUtilities {

	/**
	 * Returns a string with the same content, but all lines have shifted to the
	 * right by two characters.
	 */
	public static String tabify(String input) {
		return "  " + input.replace("\n", "\n  ");
	}
	
	/**
	 * Returns a string padded on the right with spaces to the specified
	 * length.
	 */
	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}

	/**
	 * Returns a string padded on the left with spaces to the specified length.
	 */
	public static String padLeft(String s, int n) {
	    return String.format("%1$" + n + "s", s);  
	}
	
	/**
	 * Returns a string padded on the right and left with spaces to the
	 * specified length.
	 */
	public static String padCenter(String text, int len){
	    String out = String.format("%"+len+"s%s%"+len+"s", "",text,"");
	    float mid = (out.length()/2);
	    float start = mid - (len/2);
	    float end = start + len; 
	    return out.substring((int)start, (int)end);
	}
	
	/**
	 * Returns a string in which newline characters and tab characters are
	 * replaced with their escaped equivalents.
	 */
	public static String escaped(String input) {
		input = input.replace("\t", "\\t");
		input = input.replace("\n", "\\n");
		return input;
	}
	
	public static char getUnescapedCharacter(String input) {
		if (input.length() == 1) {
			return input.charAt(0);
		}
		
		if (input.length() > 2) {
			String msg = "Expected length of 1 or 2, but got instead " +
					input.length() + ".";
			ErrorUtilities.throwError(msg);
		}
		
		if (input.equals("\n")) {
			return '\n';
		} else if (input.equals("\t")) {
			return '\t';
		} else if (input.equals("\\ ")) {
			return ' ';
		} else if (input.equals("\\\\")) {
			return '\\';
		}
		ErrorUtilities.throwError(
				"Did not recognize the escaped sequence " + input);
		return 0;
	}
	
	public static void removeLeadingWhitespace(StringBuffer strbuf) {
		while (strbuf.length() > 0 &&
				Character.isWhitespace(strbuf.charAt(0)) &&
				strbuf.charAt(0) != '\n') {
			strbuf.deleteCharAt(0);
		}
	}
	
	public static String removeQuotes(String strBuf)
	{
		return strBuf.substring(1, strBuf.length() - 1);
	}
}
