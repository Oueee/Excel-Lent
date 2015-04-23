package statistics;

import java.io.IOException;
import java.util.Arrays;
import util.Log;

/**
 * CDS class
 *
 * To use, first call the constructor with an appropriate header. This will
 * create an empty object, ready to be sent data to analyse. Then call the add
 * method repeatedly to add data. The object analyses the data, updating the
 * frequency tables. After all data has been added, if the method
 * isCompleteAndCorrect returns true, it is safe to use the obtained
 * frequencies.
 */
public class CDS {

	private Header header;

	// length of all data added until now
	private int length = 0;

	// array holding the frequencies of the respective trinucleotides
	public int[] phase0FrequencyTable = new int[64];
	public int[] phase1FrequencyTable = new int[64];
	public int[] phase2FrequencyTable = new int[64];

	// constants for bases
	private static final byte BASE_A = 65;
	private static final byte BASE_C = 67;
	private static final byte BASE_G = 71;
	private static final byte BASE_T = 84;

	// phase of the next trinucleotide to be read
	private byte phaseOfNextTrinucleotide = 0;

	// last three bytes from the last line, to calculate phases "split" between added lines
	private byte[] lastThreeBytesFromLastLine = new byte[3];

	private boolean beginningIsStartCodon = false;

	/**
	 * Constructor that creates an empty data array based on the header
	 * information. Call add method later to fill with data.
	 *
	 * @param header
	 */
	public CDS(Header header) {
		this.header = header;
	}

	/**
	 * Method that adds strings to the CDS data array
	 *
	 * @param line
	 * @throws IOException
	 *             if there is a problem reading the data
	 */
	/*public void add(String line) throws IOException {
		StringReader reader = new StringReader(line);
		int singleChar = reader.read();
		while (singleChar != -1) {
			if (dataPointer < data.length) {
				// A = 65, C = 67, G = 71, T = 84
				data[dataPointer] = (byte) singleChar;
				singleChar = reader.read();
				dataPointer++;
			} else {
				// CDS data does not correspond to what was expected according
				// to the header. Setting the pointer to a value higher than the
				// array length signals this, and prevents future data from
				// being added
				dataPointer = data.length + 1;
			}
		}
		reader.close();
	}*/

	/**
	 * Method that adds strings to the CDS data array
	 *
	 * @param line
	 * @throws IOException
	 *             if there is a problem reading the data
	 */
	public void add(String line) throws IOException {
		// keep track of position in string
		int position = 0;

		if (lastThreeBytesFromLastLine[0] != 0) {
			// There was at least one line before this in the CDS

			// Count the trinucleotide consisting of the last two elements from the last line + first element of this line
			this.count(phaseOfNextTrinucleotide%3, lastThreeBytesFromLastLine[1],
					lastThreeBytesFromLastLine[2], (byte) line.charAt(0));
			phaseOfNextTrinucleotide++;

			// Count the trinucleotide consisting of the last element of the last line + first two elements of this line
			if (line.length() >= 2) {
				this.count(phaseOfNextTrinucleotide%3,
						lastThreeBytesFromLastLine[2], (byte) line.charAt(0),
						(byte) line.charAt(1));
				phaseOfNextTrinucleotide++;
			}
		} else {
			// This is the first line of the CDS. Check if it begins with a start codon
			this.beginningIsStartCodon = this.checkIfStartCodon(new byte[] {
					(byte) line.charAt(position), (byte) line.charAt(position + 1),
					(byte) line.charAt(position + 2) });
		}

        // go through string, counting trinucleotides
		while (position + 2 < line.length()) {
			count(phaseOfNextTrinucleotide%3,
					(byte) line.charAt(position),
					(byte) line.charAt(position + 1),
					(byte) line.charAt(position + 2));
			phaseOfNextTrinucleotide++;
			position++;
		}

		if (line.length() == 1) {
			lastThreeBytesFromLastLine[0] = lastThreeBytesFromLastLine[1];
			lastThreeBytesFromLastLine[1] = lastThreeBytesFromLastLine[2];
			lastThreeBytesFromLastLine[2] = (byte) line.charAt(0); // = position
		} else if (line.length() == 2) {
			lastThreeBytesFromLastLine[0] = lastThreeBytesFromLastLine[2];
			lastThreeBytesFromLastLine[1] = (byte) line.charAt(0); // = position
			lastThreeBytesFromLastLine[2] = (byte) line.charAt(1); // = position + 1											// + 1
		} else {
			lastThreeBytesFromLastLine[0] = (byte) line.charAt(position - 1);
			lastThreeBytesFromLastLine[1] = (byte) line.charAt(position);
			lastThreeBytesFromLastLine[2] = (byte) line.charAt(position + 1);
		}

		// update length
		this.length += line.length();
	}

	private boolean checkIfStartCodon(byte[] trinucleotide) {
		// possible start codons
        byte [][] start = {{BASE_A, BASE_T, BASE_G},
        {BASE_C, BASE_T, BASE_G},
        {BASE_T, BASE_T, BASE_G},
        {BASE_G, BASE_T, BASE_G},
        {BASE_A, BASE_T, BASE_A},
        {BASE_A, BASE_T, BASE_C},
        {BASE_A, BASE_T, BASE_T},
        {BASE_T, BASE_T, BASE_A}};
		for (byte[] startelem : start) {
			if (Arrays.equals(trinucleotide,startelem))
				return true;
			}
		// else
		return false;
	}

	/**
	 * Count a trinucleotide by incrementing its value in the frequency table (decrementing if the last parameter is true)
	 * @param phase
	 * @param first
	 * @param second
	 * @param third
	 * @param decrement
	 */
	private void count(int phase, byte first, byte second, byte third, boolean decrement) {
		int index_of_trinucleotide = 0;
		// Axx: from 0 to 15; Cxx: from 16 to 31; Gxx: from 32 to 47; Txx: from
		// 48 to 64
		switch (first) {
		case BASE_T:
			index_of_trinucleotide += 16;
		case BASE_G:
			index_of_trinucleotide += 16;
		case BASE_C:
			index_of_trinucleotide += 16;
		case BASE_A:
			break;
		default:
			// TODO throw exception
		}
		// xA: from 0 to 3 / 16 to 19 / 32 to 35 / 48 to 51
		switch (second) {
		case BASE_T:
			index_of_trinucleotide += 4;
		case BASE_G:
			index_of_trinucleotide += 4;
		case BASE_C:
			index_of_trinucleotide += 4;
		case BASE_A:
			break;
		default:
			// TODO throw exception
		}
		switch (third) {
		case BASE_T:
			index_of_trinucleotide++;
		case BASE_G:
			index_of_trinucleotide++;
		case BASE_C:
			index_of_trinucleotide++;
		case BASE_A:
			break;
		default:
			// TODO throw exception
		}
		switch (phase) {
		case 0:
			if (!decrement)
				phase0FrequencyTable[index_of_trinucleotide]++;
			else
				phase0FrequencyTable[index_of_trinucleotide]--;
			break;
		case 1:
			if (!decrement)
				phase1FrequencyTable[index_of_trinucleotide]++;
			else
				phase1FrequencyTable[index_of_trinucleotide]--;
			break;
		case 2:
			if (!decrement)
				phase2FrequencyTable[index_of_trinucleotide]++;
			else
				phase2FrequencyTable[index_of_trinucleotide]--;
			break;
		}
	}

	private void count(int phase, byte first, byte second, byte third) {
		this.count(phase, first, second, third, false);
	}

	/**
	 * This method performs statistical analyses on the CDS data
	 *
	 * TODO This basic implementation writes some statistics to the standard
	 * output
	 *
	 * However, the goal is that the method will eventually probably return
	 * some object which can later be written to an Excel file so that the CDS
	 * data itself can be deleted from memory
	 */
	/*
	public void analyse() {
		int freqA = 0;
		int freqC = 0;
		int freqG = 0;
		int freqT = 0;

		int i = 0;
		outer: while (i < data.length) {
			switch (data[i]) {
			case BASE_A:
				freqA++;
				break;
			case BASE_C:
				freqC++;
				break;
			case BASE_G:
				freqG++;
				break;
			case BASE_T:
				freqT++;
				break;
			default:
				break outer;
			}
			i++;
		}

		System.out.println("Frequencies of characters: A: " + freqA + ", C: "
				+ freqC + ", G: " + freqG + ", T: " + freqT);
	}*/

    /** This method compute all trinucleotide occurence in each phase
     * based on the data array
     **/
    /*public void analyse_trinucleotide()
    {
        int len = data.length;
        int phase = 0;
        //in the loop we start in the last elem of the phase 0 and we decrease
        for(int i = 0; i <= len-4; i++)
        {
            //create the trinucleotide :simplification of real it's byte and not letter
            byte [] trinucleotide = {data[i], data[i+1], data[i+2]};
            // add one in the array/hashtable/object of the phase
            // "phase" and the trinucleotide "trinucleotide"

            //changing of phase
            phase = (phase+1)%3;
        }
    }*/

	/**
	 * This method returns true if the CDS has been filled with exactly the
	 * amount of data that was expected according to its header
	 * => This amount of data prove that all data have been found
     *
	 * TODO better checks, such as START and STOP codons
     *
	 * @return
	 */
	public boolean isCompleteAndCorrect() {
		// check if length is the same as expected from header
		if (length != header.getExpectedCDSLength()) {
			Log.w("CDS BAD: Unexpected CDS length of " + length + " (expected: " + header.getExpectedCDSLength());
			return false;
		}

		if (!beginningIsStartCodon) {
			Log.w("CDS BAD: Beginning is not start codon");
			return false;
		}

		// check if the end is a stop codon
		boolean lastTrinucleotideEqualsAStopCodon = false;
        byte [][] stop = {{BASE_T, BASE_A, BASE_A},
                           {BASE_T, BASE_A, BASE_G},
                           {BASE_T, BASE_G, BASE_A}};
        for(byte[] stopelem : stop)
        {
            if (Arrays.equals(lastThreeBytesFromLastLine,stopelem))
            {
                lastTrinucleotideEqualsAStopCodon = true;
                break;
            }
        }
		if (lastTrinucleotideEqualsAStopCodon == false)
			return false;

        // if we have made it here, everything is fine

		// do not count stop codon
		this.count(0, lastThreeBytesFromLastLine[0],
				lastThreeBytesFromLastLine[1], lastThreeBytesFromLastLine[2],
				true);
        return true;
	}

	public int[] getPhase0FrequencyTable() {
		return phase0FrequencyTable;
	}

	public int[] getPhase1FrequencyTable() {
		return phase1FrequencyTable;
	}

	public int[] getPhase2FrequencyTable() {
		return phase2FrequencyTable;
	}

}
