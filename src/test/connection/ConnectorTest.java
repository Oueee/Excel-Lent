package connection;

import java.io.IOException;

import org.junit.Test;

public class ConnectorTest {

	@Test
	public void testConnector() throws IOException {

		Connector c = new Connector();
		c.downloadAndAnalyseReplicon("NC_021245.1");

	}
}
