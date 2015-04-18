package core;

import gui.ProgressBarListener;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.File;
import java.util.Map;

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
	// informations about the species
	private File kingdomDir;
	private Map specieInfos;
	private String subGroupName;
	private String speciesName;
	private Set<String> repliconIDs;

	// to be able to update the progress bar
	private ThreadPoolExecutor es;
	private ProgressBarListener listener;

	public SpeciesManager(File kingdom, Map specie,
			    ThreadPoolExecutor es, ProgressBarListener listener) {
		
		this.kingdomDir = kingdom;
		this.specieInfos = specie;
		this.es = es;
		this.listener = listener;
		
		/* Tips: 
         * to get a children of FILE, append a String at the File : new File(File, String)
         * to get a descendant, append many String at a File: util.PathUtils.join(File, String...)
         */
         
         /*
          * kingdomDir (aboslute path)
          * specieInfos (Map):
          *      name        (String)
          *      replicons   (Set<String>)
          *      modify_date (String)
          *      group       (String)
          *      subGroup    (String)
          */
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