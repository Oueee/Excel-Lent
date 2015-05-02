package statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class CDSTest {

	@Test
	public void testCDS() throws IOException {
		String headerString = ">foo|bar [gene=baz] [protein=foo] [protein_id=bar] [location=1..3]";
		Header header = new Header(headerString);
		CDS cds = new CDS(header);

		cds.add("ATGTAA");
		AnalysisResults results = new AnalysisResults();
		assertFalse(cds.isCompleteAndCorrect());
		
		headerString = ">foo|bar [gene=baz] [protein=foo] [protein_id=bar] [location=1..6]";
		header = new Header(headerString);
		cds = new CDS(header);

		cds.add("ATGTAA");
		results = new AnalysisResults();
		assertTrue(cds.isCompleteAndCorrect());
		
		results.update(cds);
		System.out.println("ACGACG results:");
		System.out.println(results);
		assertEquals(1, (int) results.getPhase0Frequencies().get("ATG"));
		assertEquals(1, (int) results.getPhase1Frequencies().get("TGT"));
		assertEquals(0, (int) results.getPhase2Frequencies().get("TAA"));
	}
	

	@Test
	public void testCDS2() throws IOException {
		String headerString = ">foo|bar [gene=baz] [protein=foo] [protein_id=bar] [location=1..21]";
		Header header = new Header(headerString);
		CDS cds = new CDS(header);

		cds.add("ATGAATAAT");
		cds.add("CTGATGCCC");
		cds.add("TAA");
		
		AnalysisResults results = new AnalysisResults();
		assertTrue(cds.isCompleteAndCorrect());
		
		results.update(cds);
		System.out.println("Test 2 results:");
		System.out.println(results);
		assertEquals(2, (int) results.getPhase0Frequencies().get("ATG"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("AAT"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("CTG"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("CCC"));
		
		// all other must be 0
		for (Map.Entry<String, Integer> entry : results.getPhase0Frequencies().entrySet()) {
			if (!entry.getKey().matches("ATG|AAT|CTG|CCC"))
				assertEquals(0, (int) entry.getValue());
		}
		
		assertEquals(2, (int) results.getPhase1Frequencies().get("TGA"));
		assertEquals(1, (int) results.getPhase1Frequencies().get("ATA"));
		assertEquals(1, (int) results.getPhase1Frequencies().get("ATC"));
		assertEquals(1, (int) results.getPhase1Frequencies().get("TGC"));
		assertEquals(1, (int) results.getPhase1Frequencies().get("CCT"));
		
		// all other must be 0
		for (Map.Entry<String, Integer> entry : results.getPhase1Frequencies().entrySet()) {
			if (!entry.getKey().matches("TGA|ATA|ATC|TGC|CCT"))
				assertEquals(0, (int) entry.getValue());
		}
		
		assertEquals(1, (int) results.getPhase2Frequencies().get("GAA"));
		assertEquals(1, (int) results.getPhase2Frequencies().get("TAA"));
		assertEquals(1, (int) results.getPhase2Frequencies().get("TCT"));
		assertEquals(1, (int) results.getPhase2Frequencies().get("GAT"));
		assertEquals(1, (int) results.getPhase2Frequencies().get("GCC"));
		assertEquals(1, (int) results.getPhase2Frequencies().get("CTA"));
		
		// all other must be 0
		for (Map.Entry<String, Integer> entry : results.getPhase2Frequencies().entrySet()) {
			if (!entry.getKey().matches("GAA|TAA|TCT|GAT|GCC|CTA"))
				assertEquals(0, (int) entry.getValue());
		}
	}
	
	@Test
	public void testCDS3() throws IOException {
		String headerString = ">foo|bar [gene=baz] [protein=foo] [protein_id=bar] [location=1..144]";
		Header header = new Header(headerString);
		CDS cds = new CDS(header);
/*
		cds.add("ATG AAT AAT GTC GAA ATA TCT CGC GAC GAG TGT AAG GCT ATG TCT CAT GGT CTG TAT TGT CGG GTG GCT T");
		cds.add("CT TAA ATG CCC AGT TAC AAA GAG CTG GAT GGT TGG TGC AGT TCG CTG TCG TCC CTA GAA AGG GAC AAA AGC A");
	*/
		
		cds.add("ATGAATAATGTCGAAATATCTCGCGACGAGTGTAAGGCTATGTCTCATGGTCTGTATTGTCGGGTGGCTT");
		cds.add("CTTAAATGCCCAGTTACAAAGAGCTGGATGGTTGGTGCAGTTCGCTGTCGTCCCTAGAAAGGGACAAAAGCTAA");
		
		AnalysisResults results = new AnalysisResults();
		assertTrue(cds.isCompleteAndCorrect());
		results.update(cds);
		
		System.out.println("Test 3 results:");
		System.out.println(results);
		
		assertEquals(3, (int) results.getPhase0Frequencies().get("ATG"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("AAT"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("GTC"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("GAA"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("ATA"));
		assertEquals(3, (int) results.getPhase0Frequencies().get("TCT"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("CGC"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("GAC"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("GAG"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("TGT"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("GCT"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("CAT"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("GGT"));
		assertEquals(3, (int) results.getPhase0Frequencies().get("CTG"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("TAT"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("CGG"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("GTG"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("CCC"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("AGT"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("TAC"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("AAA"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("GAT"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("TGG"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("TGC"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("TGC"));
		assertEquals(2, (int) results.getPhase0Frequencies().get("TCG"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("TCC"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("CTA"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("AGG"));
		assertEquals(1, (int) results.getPhase0Frequencies().get("AGC"));

	}
}
