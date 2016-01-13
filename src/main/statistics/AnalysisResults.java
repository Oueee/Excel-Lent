package statistics;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class AnalysisResults {

    public static final String[] CDS_STRINGS = { "AAA", "AAC", "AAG", "AAT", "ACA",
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

    public Map<String, Integer> phase0Preferences = new TreeMap<String, Integer>();
    public Map<String, Integer> phase1Preferences = new TreeMap<String, Integer>();
    public Map<String, Integer> phase2Preferences = new TreeMap<String, Integer>();

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

    private int isPrefered(int p1, int p2, int p3, int index) {
        switch (index) {
            case 0:  return (p1 >= p2 && p1 >= p3) ? 1 : 0;
            case 1:  return (p2 >= p1 && p2 >= p3) ? 1 : 0;
            default: return (p3 >= p1 && p3 >= p2) ? 1 : 0;
        }
    }

    public void update(CDS cds) {
        for (int i = 0; i < 64; i++) {
            int phase0 = cds.getPhase0FrequencyTable()[i];
            int phase1 = cds.getPhase1FrequencyTable()[i];
            int phase2 = cds.getPhase2FrequencyTable()[i];

            if (noCdsTraitees != 0) {
                phase0Frequencies.put(CDS_STRINGS[i], phase0Frequencies.get(CDS_STRINGS[i]) + phase0);
                phase1Frequencies.put(CDS_STRINGS[i], phase1Frequencies.get(CDS_STRINGS[i]) + phase1);
                phase2Frequencies.put(CDS_STRINGS[i], phase2Frequencies.get(CDS_STRINGS[i]) + phase2);

                phase0Preferences.put(CDS_STRINGS[i], phase0Preferences.get(CDS_STRINGS[i]) + isPrefered(phase0, phase1, phase2, 0));
                phase1Preferences.put(CDS_STRINGS[i], phase1Preferences.get(CDS_STRINGS[i]) + isPrefered(phase0, phase1, phase2, 1));
                phase2Preferences.put(CDS_STRINGS[i], phase2Preferences.get(CDS_STRINGS[i]) + isPrefered(phase0, phase1, phase2, 2));
            } else {
                phase0Frequencies.put(CDS_STRINGS[i], phase0);
                phase1Frequencies.put(CDS_STRINGS[i], phase1);
                phase2Frequencies.put(CDS_STRINGS[i], phase2);

                phase0Preferences.put(CDS_STRINGS[i], isPrefered(phase0, phase1, phase2, 0));
                phase1Preferences.put(CDS_STRINGS[i], isPrefered(phase0, phase1, phase2, 1));
                phase2Preferences.put(CDS_STRINGS[i], isPrefered(phase0, phase1, phase2, 2));
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
