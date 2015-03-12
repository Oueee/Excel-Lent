package connection;

import java.util.EventListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import util.Log;
import org.json.JSONObject;


/**
 * Manages the species
 * 
 * Downloads the list of species and determines which have to be updated
 */
public class GenomeManager {
	private File root_path;
	private URL url;
	
	private static final int BUFFER_SIZE = 4096;
	
	public GenomeManager (File root_path, URL url) {
        this.root_path = root_path;
        this.url = url;
        
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
    	// TODO add species threads like this:
		//es.execute(new SpeciesManager("foobar", new HashSet<String>(Arrays.asList("NC_003424.3")), es, listener));
        try{
            JSONObject newSpecies = getSpecies();
        } catch(IOException e) {
            Log.e(e);
        }
    }
    
    private JSONObject getSpecies() throws IOException {
        JSONObject JSONspecies = new JSONObject();
        Map species = new HashMap();
        
        HttpURLConnection httpConn = (HttpURLConnection) this.url.openConnection();
        int responseCode = httpConn.getResponseCode();
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            String currentLine = "";
            String temp;
            int endLinePos;
            boolean header = true;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                temp = new String(buffer, "UTF-8");
                endLinePos = 0;
                
                do{
                    endLinePos = temp.indexOf("\n");
                    
                    //We haven't finish the specie/line
                    if(endLinePos == -1)
                        currentLine += temp;
                        
                    else {
                        currentLine += temp.substring(0, endLinePos);
                        if(header)
                            
                        Log.d(parseSpecie(currentLine));
                        
                        currentLine = "";
                        temp = temp.substring(endLinePos+1, temp.length());
                    }
                    
                }while(endLinePos != -1);
            }
 
            inputStream.close();
        } else {
            Log.e("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
        
        JSONspecies.put("species", species);
        return JSONspecies;
    }
    
    private String parseHeader(String line) {
        
        return "";
    }
    
    private String parseSpecie(String line) {
        return line;
    }
}