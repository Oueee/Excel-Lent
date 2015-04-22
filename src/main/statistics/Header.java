package statistics;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.*;


import util.StringUtils;

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
			this.isWellFormed();
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

		type = StringUtils.readUntil(reader, builder, '|');
		code = StringUtils.readUntil(reader, builder, ' ');

		// skip the ' [gene='
		reader.skip(6);

		gene = StringUtils.readUntil(reader, builder, ']');

		// skip the '] [protein='
		reader.skip(10);

		protein = StringUtils.readUntil(reader, builder, ']');

		// skip the '] [protein_id='
		reader.skip(13);

		proteinID = StringUtils.readUntil(reader, builder, ']');

		// skip the '] [location='
		reader.skip(11);

		location = StringUtils.readUntil(reader, builder, ']');

		reader.close();
	}

	/**
	 * Get the expected length of the corresponding CDS by summing the absolute
	 * differences of the pairs in the location:
	 * 
	 * e.g. location=complement(1...50) -> sum = abs(1-50) + 1
	 * location=complement(join(4...60),join(56..70)) -> sum = abs(4-60) + 1
	 * abs(56-70) + 1
	 * 
	 * @return
	 */
	public int getExpectedCDSLength() {
		List<Integer> list = StringUtils.findNumbersInString(this.location);

		if (list.size() % 2 != 0) {
			throw new IllegalArgumentException(
					"Method needs a list with an even number of elements");
		}

		int i = 0;
		int sum = 0;
		while (i < list.size()) {
			sum += Math.abs(list.get(i) - list.get(i + 1)) + 1;
			i += 2;
		}

		return sum;
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

	// TODO possibly do more checks
    /** Check is the location is well formed 
     * it should be something like : complement(join(2..34,42..4200))
     * /!\ Only one complement and one join and .. to separate two number
     * in the string a..b,c..d  a < b, b < c, c < d
     * 
     * change wellFormed of the class
     * @return a boolean showing if the string is well formed or not
    **/
	public boolean isWellFormed() {
        //check if the form match
		//TODO does not work e.g. it says that the following is malformed: >lcl|NC_021245.1_cds_YP_008003511.1_1 [gene=AV2] [protein=pre-coat protein] [protein_id=YP_008003511.1] [location=133..483]
        // => work now
        Pattern pattern = Pattern.compile("^(\\d+\\.\\.\\d+)|(complement\\((\\d+\\.\\.\\d+)\\)|complement\\(join\\((\\d+\\.\\.\\d+,)*(\\d+\\.\\.\\d+)\\)\\))$");
        Matcher matcher = pattern.matcher(this.location);
        boolean match = matcher.matches();
        if (match == false)
        {
        	System.out.println("No match for header pattern");
            this.wellFormed = false;
            return this.wellFormed;
        } 
        //check if the number are in the right order
        List<Integer> list = StringUtils.findNumbersInString(this.location);
        int last = -1;
        for (int elem: list)
        {
            if (last < elem)
                last = elem;
            else
            {
                this.wellFormed = false;
                return this.wellFormed;
            }
        }
        // check if the lenght is != %3 then elimination
        if (this.getExpectedCDSLength()%3 != 0)
        {
            this.wellFormed = false;
            return this.wellFormed;
        }
        
        
        this.wellFormed = true;
        return this.wellFormed;
	}
}
