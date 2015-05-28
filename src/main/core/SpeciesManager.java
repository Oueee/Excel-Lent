package core;

import gui.ProgressBarListener;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.swing.SwingWorker;
import java.util.Arrays;

import connection.Connector;
import statistics.AnalysisResults;
import util.PathUtils;
import util.Log;
import util.Pair;
import excel.Excel_settings;

/**
 * Manages a species:
 *
 * Downloads all the data for a replicon from the internet using the connection
 * package (which analyses it using the statistics package) and saves the
 * results using the excel package.
 */
public class SpeciesManager extends SwingWorker<Void, Void> {
	// informations about the species
	private File kingdomDir;
	private Map specieInfos;

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
	protected Void doInBackground() {
		try{

		Connector connector = new Connector();
		ArrayList<String> path = new ArrayList<String>();
		path.add((String)specieInfos.get("group"));
		path.add((String)specieInfos.get("subGroup"));
		path.add((String)specieInfos.get("name"));
		path.add((String)specieInfos.get("bioProject"));

		File path_specie = PathUtils.child_from_list(kingdomDir, path);
		File path_replicon;
		File done_file;
		AnalysisResults result;
		Excel_settings es = null;

		path.add(0, kingdomDir.getName());
		for (Pair<String,String> replicon: (Set<Pair<String,String>>) specieInfos.get("replicons")) {
		  String repliconID = replicon.y;
		  String repliconType = replicon.x;
		  
			path.add(repliconID);

			path_replicon = PathUtils.child(path_specie, repliconID, repliconType + Excel_settings.extension);

			//If we did it, we pass at the next one
			if(path_replicon.exists())
				continue;

			result 				= connector.downloadAndAnalyseReplicon(repliconID);
			es		 				= new Excel_settings(path_replicon, path);

			Excel_settings.update_helper(es,
												 (TreeMap)result.getPhase0Frequencies(),
												 (TreeMap)result.getPhase2Frequencies(),
							   				 (TreeMap)result.getPhase1Frequencies(),
												 result.getNoCdsTraitees(),
												 result.getNoCdsNonTraitees());
      Log.d(path);
			path.remove(path.size()-1);
		}
	}	catch(Exception e){
			Log.e(e);
			Log.exit();
		}

		return null;
	}

	// After all work is done, update the progress bar: Set its value to the
	// percentage of completed tasks
	@Override
	protected void done() {
		double completed = (double) (es.getCompletedTaskCount())
				/ (double) es.getTaskCount();
				Log.i("[" + es.getCompletedTaskCount() + "/" + es.getTaskCount() + "] " +
							(String) specieInfos.get("name"));

		int progress = (int) (100 * completed);
		listener.setProgress(progress);
		toDo(kingdomDir, specieInfos, true);
	}

	public static void toDo(File kingdom, Map specie) {
		toDo(kingdom, specie, false);
	}

	public static void toDo (File kingdom, Map specie, Boolean option) {
		File done_file = getFile(kingdom, specie);

		if(option)
		{
			try {done_file.createNewFile();}
			catch(IOException e){
				Log.e(done_file);
				Log.e(e);
				Log.exit();
			}
		}
		else
			done_file.delete();
	}

	public static boolean isDone(File kingdom, Map specie) {
		File done_file = getFile(kingdom, specie);

		return done_file.exists();
	}

	private static File getFile(File kingdom, Map specie) {
		return PathUtils.child(kingdom,
													 (String) specie.get("group"),
													 (String) specie.get("subGroup"),
													 (String) specie.get("name"),
													 (String) specie.get("bioProject"),
													 "stats"+ Excel_settings.extension);
	}
}
