package statistics;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

public class CDS {

	private Header header;
	private byte[] data;
	private int dataPointer = 0;

	public CDS(Header header) {
		this.header = header;
		// TODO initialise byte array to correct length
		data = new byte[100000];
	}

	public void analyse() {
		// TODO do something fancy, write to Excel

		int freqA = 0;
		int freqC = 0;
		int freqG = 0;
		int freqT = 0;

		int i = 0;
		outer: while (i < data.length) {
			switch (data[i]) {
			case 65:
				freqA++;
				break;
			case 67:
				freqC++;
				break;
			case 71:
				freqG++;
				break;
			case 84:
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

	public void add(String line) throws IOException {
		StringReader reader = new StringReader(line);
		int singleChar = reader.read();
		while (singleChar != -1) {
			// A = 65, C = 67, G = 71, T = 84
			// TODO do a better conversion / encoding here
			data[dataPointer] = (byte) singleChar;
			singleChar = reader.read();
			dataPointer++;
		}
		reader.close();
	}

}
