package statistics;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class AnalysisResults {

	private static final String[] CDS_STRINGS = { "AAA", "AAC", "AAG", "AAT", "ACA",
		"ACC", "ACG", "ACT", "AGA", "AGC", "AGG", "AGT", "ATA", "ATC",
		"ATG", "ATT", "CAA", "CAC", "CAG", "CAT", "CCA", "CCC", "CCG",
		"CCT", "CGA", "CGC", "CGG", "CGT", "CTA", "CTC", "CTG", "CTT",
		"GAA", "GAC", "GAG", "GAT", "GCA", "GCC", "GCG", "GCT", "GGA",
		"GGC", "GGG", "GGT", "GTA", "GTC", "GTG", "GTT", "TAA", "TAC",
		"TAG", "TAT", "TCA", "TCC", "TCG", "TCT", "TGA", "TGC", "TGG",
		"TGT", "TTA", "TTC", "TTG", "TTT", };

	private Map<String, Integer> phase0Frequencies = new TreeMap<String, Integer>();
	private Map<String, Integer> phase1Frequencies = new TreeMap<String, Integer>();
	private Map<String, Integer> phase2Frequencies = new TreeMap<String, Integer>();

	private int noCdsTraitees = 0;
	private int noCdsNonTraitees = 0;

	public int getNoCdsTraitees() {
		return noCdsTraitees;
	}

	public int getNoCdsNonTraitees() {
		return noCdsNonTraitees;
	}

	public Map<String, Integer> getPhase0Frequencies() {
		return phase0Frequencies;
	}

	public Map<String, Integer> getPhase1Frequencies() {
		return phase1Frequencies;
	}

	public Map<String, Integer> getPhase2Frequencies() {
		return phase2Frequencies;
	}

	public void update(CDS cds) {
		for (int i = 0; i < 64; i++) {
			if (noCdsTraitees != 0) {
				phase0Frequencies.put(CDS_STRINGS[i], phase0Frequencies.get(CDS_STRINGS[i]) + cds.getPhase0FrequencyTable()[i]);
				phase1Frequencies.put(CDS_STRINGS[i], phase1Frequencies.get(CDS_STRINGS[i]) + cds.getPhase1FrequencyTable()[i]);
				phase2Frequencies.put(CDS_STRINGS[i], phase2Frequencies.get(CDS_STRINGS[i]) + cds.getPhase2FrequencyTable()[i]);
			} else {
				phase0Frequencies.put(CDS_STRINGS[i], cds.getPhase0FrequencyTable()[i]);
				phase1Frequencies.put(CDS_STRINGS[i], cds.getPhase1FrequencyTable()[i]);
				phase2Frequencies.put(CDS_STRINGS[i], cds.getPhase2FrequencyTable()[i]);
			}
		}
		noCdsTraitees++;
	}

	public void foundBadCDS() {
		this.noCdsNonTraitees++;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CDS traitées: " + noCdsTraitees + "\n");
		builder.append("CDS non traitées: " + noCdsNonTraitees + "\n");
		builder.append("Phase 0 frequencies:\n" + Arrays.toString(phase0Frequencies.entrySet().toArray()) + "\n");
		builder.append("Phase 1 frequencies:\n" + Arrays.toString(phase1Frequencies.entrySet().toArray()) + "\n");
		builder.append("Phase 2 frequencies:\n" + Arrays.toString(phase2Frequencies.entrySet().toArray()) + "\n");
		return builder.toString();
	}

}
