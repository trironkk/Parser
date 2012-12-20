package utilities;

/**
 * This class contains static methods that simplify steps to handle and log
 * errors.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class ErrorUtilities {
	
	/**
	 * Prints out an error message including the specified message and then
	 * terminates the program.
	 */
	public static void throwError(String string) {
		System.out.println("\nERROR: " + string + "\n");
		
		System.out.println("Stack trace:");
		StackTraceElement[] elements = new Throwable().getStackTrace();
		for (int i = 1; i < elements.length; i++)
			System.out.println(elements[i]);
		
		System.exit(1);
	}
}
