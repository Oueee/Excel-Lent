package util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

	/**
	 * Iterates over a string and returns all numbers in the string as a list
	 * 
	 * @param string
	 * @return
	 */
	public static List<Integer> findNumbersInString(String string) {
		List<Integer> list = new ArrayList<Integer>();

		StringReader reader = new StringReader(string);

		StringBuilder builder = new StringBuilder();
		int current;
		char currentChar;
		try {
			while ((current = reader.read()) != -1) {
				currentChar = (char) current;
				switch (currentChar) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					// add to number
					builder.append(currentChar);
					break;
				default:
					// if a number has been built, save it
					if (builder.length() != 0) {
						list.add(Integer.parseInt(builder.toString()));
						builder.setLength(0);
					}
					// ignore the char
				}
			}
			// finish
			if (builder.length() != 0) {
				list.add(Integer.parseInt(builder.toString()));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
	
	/**
	 * Reads from the StringReader and writes to a StringBuilder until an
	 * expected symbol comes
	 * 
	 * @param reader
	 * @param builder
	 *            Reset at the end of the method
	 * @param expected
	 * @return the subset of the string before the expected symbol
	 * @throws IOException if the expected symbol does not come before the end of the string
	 */
	public static String readUntil(StringReader reader, StringBuilder builder,
			char expected) throws IOException {
		int symbol = reader.read();

		while ((char) symbol != expected) {
			// reached the end of string before the expected symbol came. header
			// is malformed!
			if (symbol == -1) {
				throw new IOException();
			}

			// everything ok
			builder.append((char) symbol);
			symbol = reader.read();
		}
		String result = builder.toString();
		builder.setLength(0);
		return result;
	}
}
