package connection;

import java.io.IOException;

import org.junit.Test;

public class ConnectorTest {

	@Test
	public void testConnector() throws IOException {
		long startTime = System.nanoTime();
		Connector c = new Connector();
		//System.out.println(c.downloadAndAnalyseReplicon("NC_021245.1"));
		//System.out.println(c.downloadAndAnalyseReplicon("NC_000001.11"));
		System.out.println(c.downloadAndAnalyseReplicon("NC_001633.1"));
		
		long duration = (System.nanoTime() - startTime) / 1000000000; 
		System.out.println("Download and analysis done in " + duration + " seconds");
	}
}
