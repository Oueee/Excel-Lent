package connection;

import java.io.IOException;

import org.junit.Test;

public class ConnectorTest {

	@Test
	public void testConnector() throws IOException {
		long startTime = System.nanoTime();
		Connector c = new Connector();
		//c.downloadAndAnalyseReplicon("NC_021245.1");
		c.downloadAndAnalyseReplicon("NC_000001.11");
		
		long duration = (System.nanoTime() - startTime) / 1000000000; 
		System.out.println("Download and analysis done in " + duration + " seconds");
	}
}
