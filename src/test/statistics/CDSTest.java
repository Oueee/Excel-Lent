package statistics;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class CDSTest {

	@Test
	public void testCDS() throws IOException {
		String headerString = ">foo|bar [gene=baz] [protein=foo] [protein_id=bar] [location=1..3]";
		Header header = new Header(headerString);

		CDS cds = new CDS(header);
		
		// should fail, as no data has been added yet
		assertFalse(cds.isCompleteAndCorrect());
		
		cds.add("ACG");
		
		assertTrue(cds.isCompleteAndCorrect());
		
		// writing even more data, oops
		cds.add("ACG");
		assertFalse(cds.isCompleteAndCorrect());
		
	}
}
