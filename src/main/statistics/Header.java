package statistics;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Header for a CDS
 *
 */
public class Header {

	private String type;
	private String code;
	private String gene;
	private String protein;
	private String proteinID;
	private String location;

	// this stores whether the header is syntactically well-formed
	private boolean wellFormed;

	public Header(String line) {
		try {
			this.parseHeader(line);
			wellFormed = true;
		} catch (IOException e) {
			// header malformed
			wellFormed = false;
		}
	}

	/**
	 * This method parses the header and sets the member variables. It throws an
	 * exception if it fails, to detect syntactically incorrect header.
	 * 
	 * @param line
	 * @throws IOException
	 */
	private void parseHeader(String line) throws IOException {

		// e. g. >lcl|NC_003424.3_cds_NP_592793.1_31 [gene=SPAC1F8.04c]
		// [protein=CDS] [protein_id=NP_592793.1]
		// [location=complement(92480..93871)]

		// generalised to >type|code [gene=X] [protein=X] [protein_id=X]
		// [location=X]

		StringReader reader = new StringReader(line);
		StringBuilder builder = new StringBuilder();

		// skip the ">"
		reader.skip(1);

		type = readUntil(reader, builder, '|');
		code = readUntil(reader, builder, ' ');
		
		// skip the ' [gene='
		reader.skip(6);

		gene = readUntil(reader, builder, ']');
		
		// skip the '] [protein='
		reader.skip(10);

		protein = readUntil(reader, builder, ']');

		// skip the '] [protein_id='
		reader.skip(13);

		proteinID = readUntil(reader, builder, ']');
		
		// skip the '] [location='
		reader.skip(11);

		location = readUntil(reader, builder, ']');

		reader.close();
		System.out.println("finished reading header");
	}

	/**
	 * Reads from the StringReader and writes to a StringBuilder until an expected symbol comes
	 * @param reader
	 * @param builder Reset at the end of the method
	 * @param expected
	 * @return the subset of the string before the expected symbol
	 * @throws IOException
	 */
	private String readUntil(StringReader reader, StringBuilder builder,
			char expected) throws IOException {
		int symbol = reader.read();

		while ((char) symbol != expected) {
			// reached the end of string before the expected symbol came. header
			// is malformed!
			if (symbol == -1) {
				System.out.println("exceptoin");
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

	/**
	 * Get the expected length of the corresponding CDS
	 * 
	 * @return
	 */
	public int getExpectedCDSLength() {
		List<Integer> list = new ArrayList<Integer>();

		StringReader reader = new StringReader(this.getLocation());

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

		if (list.size() % 2 != 0) {
			System.out.println("List does not have an even number of elements"
					+ list.toString());
		}

		// sum the expected lengths for each pair of elements.
		int i = 0;
		int sum = 0;
		while (i < list.size()) {
			sum += Math.abs(list.get(i) - list.get(i + 1)) + 1;
			i += 2;
		}

		return sum;
	};

	public String getType() {
		return type;
	}

	public String getCode() {
		return code;
	}

	public String getGene() {
		return gene;
	}

	public String getProtein() {
		return protein;
	}

	public String getProteinID() {
		return proteinID;
	}

	public String getLocation() {
		return location;
	}

	// TODO possibly do more checks
	public boolean isWellFormed() {
		return wellFormed;
	}

}
