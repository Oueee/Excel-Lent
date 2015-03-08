package connection;

import gui.ProgressBarListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingWorker;

import statistics.CDS;
import statistics.Header;

/**
 * Manages the species
 * 
 * 
 * 
 */
public class SpeciesManager extends SwingWorker {

	// name of the species
	private String speciesName;
	private Set<String> repliconIDs = new HashSet<String>(
			Arrays.asList("NC_003424.3"));

	// to be able to update the progress bar
	private ThreadPoolExecutor es;
	private ProgressBarListener listener;

	private static final String URL_PREFIX = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=";
	private static final String URL_SUFFIX = "&rettype=fasta_cds_na&retmode=text";

	private static final int BUFFER_SIZE = 4096;

	public SpeciesManager(String name, Set<String> repliconIDs,
			ThreadPoolExecutor es, ProgressBarListener listener) {
		this.speciesName = name;
		// this.repliconIDs = repliconIDs;
		this.es = es;
		this.listener = listener;
	}

	@Override
	protected Object doInBackground() throws Exception {
		for (String repliconID : repliconIDs) {
			System.out.println("Getting replicon for ID " + repliconID);
			getReplicon(repliconID);
		}

		// TODO just for testing
		/*
		 * parse header check header if header is good: parse ABCB... + count if
		 * it isn't: jump to next header... (>)
		 */

		Thread.sleep(1000);
		return null;
	}

	private void getReplicon(String repliconID) throws IOException {
		System.out.println("droing stuff");
		String urlString = URL_PREFIX + "NC_003424.3" + URL_SUFFIX;
		System.out.println("creating url " + urlString);
		URL url = new URL(urlString);
		// TODO Auto-generated method stub

		System.out.println("opening connection");
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();
		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// opens input stream from the HTTP connection
			System.out.println("opening connection to " + url);
			InputStream inputStream = httpConn.getInputStream();
			//int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
		    //String currentLine = "";
			//String temp;
			//int endLinePos;

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));

			String line;
			boolean ignoring = false;
			Header currentHeader;
			CDS currentCDS = null;
			while ((line = reader.readLine()) != null) {
				//System.out.println("reading line " + line);
				if (ignoring)
					continue;

				// check if header
				if (line.startsWith(">")) {
					//System.out.println("line is header");
					// last CDS is finished. process
					if (currentCDS != null)
						currentCDS.analyse();

					// analyse new header
					currentHeader = new Header(line);
					if (currentHeader.check()) {
						// header good. parse rest of CDS until next header
						ignoring = false;

						// create cds
						currentCDS = new CDS(currentHeader);
					} else {
						// header bad. ignore CDS until next header
						ignoring = true;
					}
				} else {
					// this is a CDS
					//System.out.println("adding CDS");
					currentCDS.add(line);
				}

			}
			//System.out.println(line);
			reader.close();
   
			//while ((bytesRead = inputStream.read(buffer)) != -1) {
				
				
				
				
				
				/*temp = new String(buffer, "UTF-8");
				endLinePos = 0;
				do {
					endLinePos = temp.indexOf("\n");
					// We haven't finish the specie/line
					if (endLinePos == -1)
						currentLine += temp;
					else {
						currentLine += temp.substring(0, endLinePos);
						// if (header) Log.d(parseSpecie(currentLine));
						currentLine = "";
						temp = temp.substring(endLinePos + 1, temp.length());
					}
				} while (endLinePos != -1);
				*/
			//}
			//inputStream.close();
		} else {
			// Log.e("No file to download. Server replied HTTP code: " +
			// responseCode);
		}
		httpConn.disconnect();

	}

	// set the progress bar value to the percentage of completed tasks
	@Override
	protected void done() {
		double completed = (double) (es.getCompletedTaskCount())
				/ (double) es.getTaskCount();
		System.out.println(es.getCompletedTaskCount());
		System.out.println(es.getTaskCount());
		int progress = (int) (100 * completed);
		listener.setProgress(progress);
	}

}
