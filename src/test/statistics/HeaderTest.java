package statistics;

import static org.junit.Assert.*;

import org.junit.Test;

public class HeaderTest {

	@Test
	public void testHeader() {
		String headerString = ">lcl|NC_003424.3_cds_NP_592793.1_31 [gene=SPAC1F8.04c] [protein=CDS] [protein_id=NP_592793.1] [location=complement(92480..93871)]";
		Header header = new Header(headerString);

		assertEquals("lcl", header.getType());
		assertEquals("NC_003424.3_cds_NP_592793.1_31", header.getCode());
		assertEquals("SPAC1F8.04c", header.getGene());
		assertEquals("CDS", header.getProtein());
		assertEquals("NP_592793.1", header.getProteinID());
		assertEquals("complement(92480..93871)", header.getLocation());
		assertEquals(1392, header.getExpectedCDSLength());

		assertTrue(header.isWellFormed());
		
		headerString = ">lcl|NC_003424.3_cds_NP_595039.1_2 [gene=SPAC212.08c] [protein=CDS] [protein_id=NP_595039.1] [location=12158..12994]";
		header = new Header(headerString);

		assertEquals("lcl", header.getType());
		assertEquals("NC_003424.3_cds_NP_595039.1_2", header.getCode());
		assertEquals("SPAC212.08c", header.getGene());
		assertEquals("CDS", header.getProtein());
		assertEquals("NP_595039.1", header.getProteinID());
		assertEquals("12158..12994", header.getLocation());
		assertEquals(837, header.getExpectedCDSLength());
		
		assertTrue(header.isWellFormed());
		
		headerString = "this is certainly not a well-formed header!";
		header = new Header(headerString);
		
		assertFalse(header.isWellFormed());
		
		headerString = ">lcl|NC_000001.11_cds_NP_001005484.1_1 [gene=OR4F5] [protein=olfactory receptor 4F5] [protein_id=NP_001005484.1] [location=69091..70008]";
		header = new Header(headerString);
		
		assertEquals("lcl", header.getType());
		assertEquals("NC_000001.11_cds_NP_001005484.1_1", header.getCode());
		assertEquals("OR4F5", header.getGene());
		assertEquals("olfactory receptor 4F5", header.getProtein());
		assertEquals("NP_001005484.1", header.getProteinID());
		assertEquals("69091..70008", header.getLocation());
		assertEquals(918, header.getExpectedCDSLength());
		assertTrue(header.isWellFormed());
		
		headerString = ">lcl|NC_000001.11_cds_NP_963837.1_9304 [gene=LGALS8] [protein=galectin-8 isoform b] [protein_id=NP_963837.1] [location=join(236526071..236526115,236537497..236537585,236538879..236539089,236540564..236540683,236541654..236541710,236542761..236542787,236543560..236543648,236544750..236544915,236548012..236548161)]";
		header = new Header(headerString);
		
		assertEquals("lcl", header.getType());
		assertEquals("NC_000001.11_cds_NP_963837.1_9304", header.getCode());
		assertEquals("LGALS8", header.getGene());
		assertEquals("galectin-8 isoform b", header.getProtein());
		assertEquals("NP_963837.1", header.getProteinID());
		assertEquals("join(236526071..236526115,236537497..236537585,236538879..236539089,236540564..236540683,236541654..236541710,236542761..236542787,236543560..236543648,236544750..236544915,236548012..236548161)", header.getLocation());
		//assertEquals(918, header.getExpectedCDSLength());
		assertTrue(header.isWellFormed());
	
	}
	

}
