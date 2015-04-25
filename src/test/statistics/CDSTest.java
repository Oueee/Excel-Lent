package statistics;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class CDSTest {

	@Test
	public void testCDS() throws IOException {
		String headerString = ">foo|bar [gene=baz] [protein=foo] [protein_id=bar] [location=1..3]";
		Header header = new Header(headerString);

		CDS cds = new CDS(header);
		
		// should fail, as no data has been added yet
		//assertFalse(cds.isCompleteAndCorrect());
		
		//cds.add("ACG");
		
		//assertTrue(cds.isCompleteAndCorrect());
		
		// writing even more data, oops
		//cds.add("ACG");
		//assertFalse(cds.isCompleteAndCorrect());
		
		cds.add("ACG");
		
		AnalysisResults results = new AnalysisResults();
		results.update(cds);
		assertEquals(1, (int) results.getPhase0Frequencies().get("ACG"));
		assertEquals(0, (int) results.getPhase0Frequencies().get("AAA"));
		
		System.out.println("ACG results:");
		System.out.println(results);
		
		cds = new CDS (header);
		cds.add("ACGACG");
		results = new AnalysisResults();
		results.update(cds);
		System.out.println("ACGACG results:");
		System.out.println(results);
		assertEquals(2, (int) results.getPhase0Frequencies().get("ACG"));
		assertEquals(1, (int) results.getPhase1Frequencies().get("CGA"));
		assertEquals(1, (int) results.getPhase2Frequencies().get("GAC"));
	}
}
