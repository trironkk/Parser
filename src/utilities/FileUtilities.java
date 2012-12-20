package utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;

/**
 * This class contains static methods that simplify common file input/output
 * operations.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class FileUtilities {
	
	/**
	 * An empty private constructor. This class is only intended to be used as a
	 * collection of static methods, so we hide this constructor.
	 */
	private FileUtilities() {}
	
	/**
	 * Returns the contents of a give file.
	 */
	public static String getFileContents(String filePath) {
		StringBuilder result = new StringBuilder();
		try {
			Scanner sc = new Scanner(new File(filePath));
			while(sc.hasNextLine()){
			    result.append(sc.nextLine());
			    result.append('\n');
			}
		} catch (Exception e) {
			ErrorUtilities.throwError(e.toString());
		}
		return result.toString();
	}
	
	public static String getStreamContents(InputStream stream) {
	    java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	public static String getCurrentWorkingDirectory() {
		try {
			return new File(".").getCanonicalPath();
		} catch (IOException e) {
			ErrorUtilities.throwError(e.toString());
			return null;
		}
	}
}
