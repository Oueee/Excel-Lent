package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import statistics.CDS;
import statistics.Header;

/**
 * Manages the internet connection
 *
 */
public class Connector {


	private static final String URL_PREFIX = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=";
	private static final String URL_SUFFIX = "&rettype=fasta_cds_na&retmode=text";

	/**
	 * Downloads replicon data from the internet.
	 * 
	 * As some data sets can be huge, the results are analysed per CDS and not per replicon
	 * TODO Therefore, this method should probably return the results of the analysis
	 * @param repliconID
	 * @throws IOException
	 */
	public void downloadAndAnalyseReplicon(String repliconID) throws IOException {
		String urlString = URL_PREFIX + repliconID + URL_SUFFIX;
		URL url = new URL(urlString);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();
		
		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// opens input stream from the HTTP connection
						InputStream inputStream = httpConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));

			String line;
			boolean ignoring = false;
			Header currentHeader;
			CDS currentCDS = null;
			while ((line = reader.readLine()) != null) {
				// System.out.println("reading line " + line);
				if (ignoring)
					continue;

				// check if header
				if (line.startsWith(">")) {
					// line is header
					// last CDS has ended, so process it
					if (currentCDS != null)

						// TODO instead, the method analyse will probably return
						// some analysis result, which can be added to a list
						// and returned by this method
						currentCDS.analyse();

					// analyse new header
					currentHeader = new Header(line);
					if (currentHeader.isWellFormed()) {
						// header good. parse rest of CDS until next header
						ignoring = false;

						// create cds
						currentCDS = new CDS(currentHeader);
					} else {
						// header bad. ignore CDS until next header
						ignoring = true;
					}
				} else {
					// this is part of a CDS
					currentCDS.add(line);
				}

			}
			reader.close();
		} else {
			// Log.e("No file to download. Server replied HTTP code: " +
			// responseCode);
		}
		httpConn.disconnect();

	}
	
}