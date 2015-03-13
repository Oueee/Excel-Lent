package core;

import gui.ProgressBarListener;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;

import util.Log;
import connection.GenomeManager;
<<<<<<< HEAD
import org.json.JSONObject;
import java.net.UnknownHostException;
=======
>>>>>>> 6eefa3ee8b54390317eb27eea98a5fbe73d508b9


public class ExcelLent implements Runnable {
    private static GenomeManager virusesManager;
    private static GenomeManager eukaryotesManager;
    private static GenomeManager prokaryotesManager;

	ThreadPoolExecutor es;
	ProgressBarListener listener;

	public ExcelLent(ProgressBarListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		ThreadPoolExecutor es = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(10);
	
	    File project_root = new File(System.getProperty("user.dir"));
	    File urls_path = new File(project_root.getParent(), "urls_lists.json");
	    File tree_root = new File(project_root.getParent(), "tree");
        
        JSONObject urls = getUrls(urls_path);

        Log.i("Initializing genomes managers");
        
        try{
            virusesManager = new GenomeManager(new File(tree_root, "Viruses"),
                                                new URL(urls.get("Viruses").toString()));
            eukaryotesManager = new GenomeManager(new File(tree_root, "Eukaryotes"),
                                                new URL(urls.get("Eukaryotes").toString()));
            prokaryotesManager = new GenomeManager(new File(tree_root, "Prokaryote"),
                                                new URL(urls.get("Prokaryote").toString()));
        }catch(Exception e) {
            Log.e(e);
        }
        
<<<<<<< HEAD
        Log.i("Getting lists and checking what to update/remove");

            virusesManager.AddSpeciesThreads();
            eukaryotesManager.AddSpeciesThreads();
            prokaryotesManager.AddSpeciesThreads();
        
=======
        Log.i("Getting the lists and checking what to update");
        virusesManager.AddSpeciesThreads();
        eukaryotesManager.AddSpeciesThreads();
        prokaryotesManager.AddSpeciesThreads();

>>>>>>> 6eefa3ee8b54390317eb27eea98a5fbe73d508b9
	}
	
	
	private static JSONObject getUrls(File urls_path) {
	    String urls = null;
	    try{
            FileInputStream fis = new FileInputStream(urls_path);
            byte[] data = new byte[(int) urls_path.length()];
            fis.read(data);
            fis.close();

            urls = new String(data, "UTF-8");
        } catch(Exception e) {
            Log.e(e);
        }
        
        return (JSONObject) new JSONObject(urls);
	}
}
