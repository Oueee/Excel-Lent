package statistics;

import java.io.IOException;
import java.io.StringReader;

/**
 * CDS class
 * 
 * To use, first call the constructor with an appropriate header. This will
 * create an empty object, ready to be filled with data. Then call the add
 * method repeatedly to add data. After all data has been added, if the method
 * isCompleteAndCorrect returns true, it is safe to use the data.
 */
public class CDS {

	private Header header;

	// data array. Initially empty, then progressively filled by calls of the
	// add method
	private byte[] data;
	private static final byte BASE_A = 65;
	private static final byte BASE_C = 67;
	private static final byte BASE_G = 71;
	private static final byte BASE_T = 84;

	// the position in the data array pointing to the next empty position in the
	// data array, or its length, if it has been filled completely, or a value
	// higher than that, if there was more data than expected
	private int dataPointer = 0;

	/**
	 * Constructor that creates an empty data array based on the header
	 * information. Call add method later to fill with data.
	 * 
	 * @param header
	 */
	public CDS(Header header) {
		this.header = header;
		// initialise the byte array to the expected length
		data = new byte[header.getExpectedCDSLength()];
	}

	/**
	 * Method that adds strings to the CDS data array
	 * 
	 * @param line
	 * @throws IOException
	 *             if there is a problem reading the data
	 */
	public void add(String line) throws IOException {
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
	}

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
		return (dataPointer == data.length);
	}

}
