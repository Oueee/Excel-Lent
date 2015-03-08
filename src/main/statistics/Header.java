package statistics;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Header {

	private String type;
	private String code;
	private String gene;
	private String protein;
	private String proteinID;
	private String location;

	private boolean wellFormed = true;

	public Header(String line) {
		try {
			this.parseHeader(line);
		} catch (IOException e) {
			// header malformed
			wellFormed = false;
		}
	}

	private void parseHeader(String line) throws IOException {

		// e. g. >lcl|NC_003424.3_cds_NP_592793.1_31 [gene=SPAC1F8.04c]
		// [protein=CDS] [protein_id=NP_592793.1]
		// [location=complement(92480..93871)]

		// generalised to >type|code [gene=X] [protein=X] [protein_id=X]
		// [location=X]
		// TODO

		StringReader reader = new StringReader(line);
		StringBuilder builder = new StringBuilder();

		// skip the ">"
		reader.skip(1);
		char singleChar = (char) reader.read();
		System.out.println("reading character " + singleChar);

		while (singleChar != '|') {
			builder.append(singleChar);
			singleChar = (char) reader.read();
		}

		type = builder.toString();
		builder.setLength(0);
		//System.out.println("current builder value: " + builder.toString() + ", length: " + builder.length());

		while ((singleChar = (char) reader.read()) != ' ') {
			builder.append(singleChar);
		}

		code = builder.toString();
		builder.setLength(0);
		// skip the ' [gene='
		reader.skip(6);

		while ((singleChar = (char) reader.read()) != ']') {
			builder.append(singleChar);
		}

		gene = builder.toString();
		builder.setLength(0);
		// skip the '] [protein='
		reader.skip(10);

		while ((singleChar = (char) reader.read()) != ']') {
			builder.append(singleChar);
		}

		protein = builder.toString();
		builder.setLength(0);
		// skip the '] [protein_id='
		reader.skip(13);

		while ((singleChar = (char) reader.read()) != ']') {
			builder.append(singleChar);
		}

		proteinID = builder.toString();
		builder.setLength(0);
		// skip the '] [location='
		reader.skip(11);

		while ((singleChar = (char) reader.read()) != ']') {
			builder.append(singleChar);
		}

		location = builder.toString();

		reader.close();
		System.out.println("finished reading header");
	}

	public int getLength() {		
		List<Integer> list = new ArrayList<Integer>();
		
	    StringReader reader = new StringReader(this.getLocation());
		
		StringBuilder builder = new StringBuilder();
		int current;
		char currentChar;
		try {
			while ((current = reader.read()) != -1) {
				currentChar = (char) current;
				switch (currentChar) {
				case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
					// add to number
					builder.append(currentChar);
					break;
				default:
					// if a number has been built, save it
					if (builder.length() != 0) {
						list.add(Integer.parseInt(builder.toString()));
						builder.setLength(0);
					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		int sum = 0;
		while (i < list.size()) {
			sum += list.get(i + 1) - list.get(i);
			i += 2;
		}
		
		return sum;
	};

	public boolean check() {
		// TODO more checks here
		return wellFormed;
	}

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

	public boolean isWellFormed() {
		return wellFormed;
	}

}
