package net.cpprograms.minecraft.TravelPortals;

import java.util.HashMap;
import java.util.Map;

/**
 * DO WHAT YOU WANT TO PUBLIC LICENSE
 * Credit to angelsl for the original code.
 * Updated by Phoenix616
 */
public class MinecraftFontWidthCalculator {

	private static final int DEFAULT_WIDTH = 5;

	// Special characters not 5 dots wide
	private static final Map<Character, Integer> SPECIAL_WIDTHS = new HashMap<>();
	static {
		SPECIAL_WIDTHS.put(' ', 3);
		SPECIAL_WIDTHS.put('!', 1);
		SPECIAL_WIDTHS.put('"', 3);
		SPECIAL_WIDTHS.put('\'', 1);
		SPECIAL_WIDTHS.put('(', 3);
		SPECIAL_WIDTHS.put(')', 3);
		SPECIAL_WIDTHS.put('*', 3);
		SPECIAL_WIDTHS.put(',', 1);
		SPECIAL_WIDTHS.put('.', 1);
		SPECIAL_WIDTHS.put(':', 1);
		SPECIAL_WIDTHS.put(';', 1);
		SPECIAL_WIDTHS.put('<', 4);
		SPECIAL_WIDTHS.put('>', 4);
		SPECIAL_WIDTHS.put('@', 6);
		SPECIAL_WIDTHS.put('I', 3);
		SPECIAL_WIDTHS.put('[', 3);
		SPECIAL_WIDTHS.put(']', 3);
		SPECIAL_WIDTHS.put('`', 2);
		SPECIAL_WIDTHS.put('f', 4);
		SPECIAL_WIDTHS.put('i', 1);
		SPECIAL_WIDTHS.put('k', 4);
		SPECIAL_WIDTHS.put('l', 2);
		SPECIAL_WIDTHS.put('t', 3);
		SPECIAL_WIDTHS.put('{', 3);
		SPECIAL_WIDTHS.put('|', 1);
		SPECIAL_WIDTHS.put('}', 3);
		SPECIAL_WIDTHS.put('~', 6);
	}

	// Custom function to get the maximum width of the string.
	public static int getMaxStringWidth()
	{
		return getStringWidth("-----------------------------------------------------.");
	}


	// taken directly from notchcode. enjoy
	public static int getStringWidth(String s) {
		if (s == null) {
			return 0;
		}
		int i = 0;
		for (int j = 0; j < s.length(); j++) {
			if (s.charAt(j) == '\247') {
				j++;
				continue;
			}
			i += SPECIAL_WIDTHS.getOrDefault(s.charAt(j), DEFAULT_WIDTH);
		}

		return i;
	}
}