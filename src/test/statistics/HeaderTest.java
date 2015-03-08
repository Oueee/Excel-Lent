package statistics;

import static org.junit.Assert.*;

import org.junit.Test;

public class HeaderTest {

	@Test
	public void testConstructor() {
		String headerString = ">lcl|NC_003424.3_cds_NP_592793.1_31 [gene=SPAC1F8.04c] [protein=CDS] [protein_id=NP_592793.1] [location=complement(92480..93871)]";
		Header header = new Header(headerString);

		assertEquals("lcl", header.getType());
		assertEquals("NC_003424.3_cds_NP_592793.1_31", header.getCode());
		assertEquals("SPAC1F8.04c", header.getGene());
		assertEquals("CDS", header.getProtein());
		assertEquals("NP_592793.1", header.getProteinID());
		assertEquals("complement(92480..93871)", header.getLocation());

		
		
		headerString = ">lcl|NC_003424.3_cds_NP_595039.1_2 [gene=SPAC212.08c] [protein=CDS] [protein_id=NP_595039.1] [location=12158..12994]";
		header = new Header(headerString);

		assertEquals("lcl", header.getType());
		assertEquals("NC_003424.3_cds_NP_595039.1_2", header.getCode());
		assertEquals("SPAC212.08c", header.getGene());
		assertEquals("CDS", header.getProtein());
		assertEquals("NP_595039.1", header.getProteinID());
		assertEquals("12158..12994", header.getLocation());

	
	
	}

}
