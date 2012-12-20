package utilities;

import java.util.HashSet;
import java.util.Set;

public class CharacterUtilities {
	public static final Set<Character> lowerCaseCharacters;
	public static final Set<Character> upperCaseCharacters;
	public static final Set<Character> characters;
	
	public static final Set<Character> numbers;
	
	public static final Character[] lowerCaseCharacterArray = new Character[] {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	};
	public static final Character[] upperCaseCharacterArray = new Character[] {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
		'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
	};
	public static final Character[] numberCharacterArray = new Character[] {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	};
	
	static {
		lowerCaseCharacters = new HashSet<Character>();
		for (Character c : lowerCaseCharacterArray) {
			lowerCaseCharacters.add(c);
		}
		
		upperCaseCharacters = new HashSet<Character>();
		for (Character c : upperCaseCharacterArray) {
			upperCaseCharacters.add(c);
		}
		
		characters = new HashSet<Character>();
		characters.addAll(lowerCaseCharacters);
		characters.addAll(upperCaseCharacters);
		
		numbers = new HashSet<Character>();
		for (Character c : numberCharacterArray) {
			numbers.add(c);
		}
	}
}
