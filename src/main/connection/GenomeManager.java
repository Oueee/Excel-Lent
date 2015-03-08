package main.connection;

import java.util.EventListener;
import java.io.File;
import main.util.Log;
import java.io.IOException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Manages the species
 * 
 * Downloads the list of species and determines which have to be updated
 */
public class GenomeManager {
	private File root_path;
	
	public GenomeManager (File root_path) {
        this.root_path = root_path;
        
        //If it's the first run, we initialize it
		if(!this.root_path.exists()) {
		    Log.i("create the kingdom " + this.root_path.getName());
		    this.root_path.mkdirs();
		    
		    File done = new File(this.root_path, "done.json");
		    JSONObject emptyJSON = new JSONObject();
		    emptyJSON.put("species", new HashMap());
		    
		    try{
		        PrintWriter writer = new PrintWriter(done.getAbsolutePath(), "UTF-8");
                writer.println(emptyJSON.toString(2));
                writer.close();
		    }
		    catch(IOException e){
		        Log.e(e);
		    }
		}
    }
    
    public void AddSpeciesThreads(){//ExecutorService pool) {
        

    }
}