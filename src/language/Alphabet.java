package language;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This is an information holder class that defines the range of Characters that
 * will be considered valid for a particular Table.
 * 
 * This class also marginally improves the runtime performance with regard to
 * the length of a document to parse, as it replaces the reconstruction of
 * already existing Character objects with indexing into an array. 
 * 
 * TODO: Right now, this class operates under the assumption that the characters
 * of an alphabet will start at ASCII value 0, and go up to a predefined maximum
 * value. While the desired functionality for alphabets such as bit strings is
 * available, the intuitive notion of having an alphabet restricted to 0's and
 * 1's is impossible with this implementation.
 * 
 * @author Trironk Kiatkungwanglai
 */
public class Alphabet {
	
	/**
	 * An array of Character objects to index into.
	 */
	private final Character[] array;
	
	/**
	 * This value is equivalent to the length of the array field.
	 */
	public final int length;

	/**
	 * Constructs an alphabet a specified length, containing Characters for the
	 * ASCII values up to the specified length. 
	 */
	public Alphabet(int length) {
		this.length = length;
		
		this.array = new Character[length];
		for (int i = 0; i < array.length; i++) {
			array[i] = new Character((char)i);
		}
	}
	
	/**
	 * Returns a set that contains all characters that are legal in this
	 * alphabet.
	 * TODO: This is really a hack in place of a better, more conceptually pure
	 * design of an Alphabet. Please fix.
	 */
	public Set<Character> getAllCharacters() {
		Set<Character> result = new HashSet<Character>();
		for (int i = 0; i < array.length; i++) {
			if (Character.isLetterOrDigit(array[i]) ||
					Character.isWhitespace(array[i]) ||
					PUNCTUATION.contains(array[i])) {
				result.add(array[i]);
			}
		}
		return result;
	}
	
	/**
	 * Returns the Character object associated with the given char.
	 * 
	 * Note: When given a char that corresponds to 65535, we just return null.
	 */
	public Character get(char c) {
		if ((int)c == (1 << 16) - 1)
			return null;
		else
			return array[(int)c];
	}
	public static final Character[] PUNCTUATION_CHARACTERS =
			new Character[] {
		'[', '.', ',', '!', '?', ':', ';', '\'', '\\', '"', '-', ']', '+', '=',
		'*', '(', ')', '{', '}', '_', '<', '>', '/', '~', ' ', '#', '%', '^',
		'&', '@', '$'
	};
	public static final Set<Character> PUNCTUATION = new HashSet<Character>(
			Arrays.asList(PUNCTUATION_CHARACTERS));
}
