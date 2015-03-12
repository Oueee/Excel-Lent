package core;

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
 * Manages a species:
 * 
 * Downloads all the data for a species from the internet using the connection
 * package (which analyses it using the statistics package) and saves the
 * results using the excel package.
 */
public class SpeciesManager extends SwingWorker<Void, Void> {

	// name of the species
	private String speciesName;
	private Set<String> repliconIDs = new HashSet<String>(
			Arrays.asList("NC_003424.3"));

	// to be able to update the progress bar
	private ThreadPoolExecutor es;
	private ProgressBarListener listener;

	private static final String URL_PREFIX = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=";
	private static final String URL_SUFFIX = "&rettype=fasta_cds_na&retmode=text";

	public SpeciesManager(String name, Set<String> repliconIDs,
			ThreadPoolExecutor es, ProgressBarListener listener) {
		this.speciesName = name;
		// this.repliconIDs = repliconIDs;
		this.es = es;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground() throws Exception {
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
