package core;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.net.UnknownHostException;


import org.json.JSONObject;
import org.json.JSONObject;
import gui.ProgressBarListener;
import connection.GenomeManager;
import util.Log;


public class ExcelLent implements Runnable {
    private static GenomeManager virusesManager;
    private static GenomeManager eukaryotesManager;
    private static GenomeManager prokaryotesManager;
    private static boolean toDo[];

	ThreadPoolExecutor es;
	ProgressBarListener listener;

	public ExcelLent(ProgressBarListener listener) {
		this.listener = listener;
	}

  public void setToDo(boolean v, boolean e, boolean p) {
    this.toDo = new boolean[3];
    toDo[0] = v;
    toDo[1] = e;
    toDo[2] = p;
  }

	@Override
	public void run() {
		ThreadPoolExecutor es = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(20);

	    File project_root = new File(System.getProperty("user.dir"));

	    File urls_path = new File(project_root, "urls_lists.json");
	    File tree_root = new File(project_root, "tree");

        if(!urls_path.exists())
            Log.e("Doesn't find urls_lists.json file." +
            " Maybe you have to run the application from the " +
            "root directory of the project.");

        if(!tree_root.exists() || !tree_root.isDirectory())
            Log.e("Doesn't root of the genome tree." +
            " Maybe you have to the application from the " +
            "root directory of the project.");

        JSONObject urls = getUrls(urls_path);

        try{
          if(toDo[0]) {
            Log.i("Checking viruses");
            virusesManager = new GenomeManager(new File(tree_root, "Viruses"),
                                    new URL(urls.get("Viruses").toString()));
            virusesManager.AddSpeciesThreads(es, listener);
          }
          if(toDo[1]) {
            Log.i("Checking eukaryotes");
            eukaryotesManager = new GenomeManager(new File(tree_root, "Eukaryotes"),
                                    new URL(urls.get("Eukaryotes").toString()));
            eukaryotesManager.AddSpeciesThreads(es, listener);
          }
          if(toDo[2]) {
            Log.i("Checking prokaryote");
            prokaryotesManager = new GenomeManager(new File(tree_root, "Prokaryote"),
                                    new URL(urls.get("Prokaryote").toString()));
            prokaryotesManager.AddSpeciesThreads(es, listener);
          }

        }catch(Exception e) {
            Log.e(e);
        }
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
