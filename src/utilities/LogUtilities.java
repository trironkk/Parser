package utilities;

/**
 * This class contains static methods that simplify common logging functions.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class LogUtilities {
	
	private static boolean active;
	
	public static boolean isActive() {
		return active;
	}
	
	public static void activate() {
		active = true;
	}
	
	public static void deactivate() {
		active = false;
	}
	
	public static void log(String message) {
		if (active == false)
			return;
		
		System.out.print(message);
	}
	
	public static void logln(String message) {
		log(message + '\n');
	}
	
	public static void logln() {
		logln("");
	}
	
	public static void log() {
		log("");
	}

	public static void logln(Object object) {
		logln(object.toString());
	}
	
	public static void log(Object object) {
		log(object.toString());
	}
	
}
