package core;

import gui.ProgressBarListener;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.SwingWorker;

import connection.Connector;

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
	private Set<String> repliconIDs;

	// to be able to update the progress bar
	private ThreadPoolExecutor es;
	private ProgressBarListener listener;

	public SpeciesManager(String name, Set<String> repliconIDs,
			ThreadPoolExecutor es, ProgressBarListener listener) {
		this.speciesName = name;
		this.repliconIDs = repliconIDs;
		this.es = es;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground() throws Exception {
		Connector connector = new Connector();
		for (String repliconID : repliconIDs) {
			// TODO this method will probably return a list of analysis results.
			// do something fancy with the results
			connector.downloadAndAnalyseReplicon(repliconID);
		}

		return null;
	}

	// After all work is done, update the progress bar: Set its value to the
	// percentage of completed tasks
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
