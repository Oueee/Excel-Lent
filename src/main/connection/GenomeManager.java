package connection;

import java.util.EventListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.Scanner;
import java.net.UnknownHostException;
import java.util.Iterator;
import util.Log;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Manages the species
 * 
 * Downloads the list of species and determines which have to be updated
 */
public class GenomeManager {
	private File root_path;
	private File list_species;
	private URL url;
	
	private static final int BUFFER_SIZE = 4096;
	    
	public GenomeManager (File root_path, URL url) {
        this.root_path = root_path;
        this.list_species = new File(this.root_path, "done.json");
        this.url = url;

        //If it's the first run, we initialize it
		if(!this.root_path.exists()) {
		    Log.i("create the kingdom " + this.root_path.getName());
		    this.root_path.mkdirs();
		    
		    writeDoneFile("{}");
		}
    }
    
    public void AddSpeciesThreads(){//ExecutorService pool) {
    	// TODO add species threads like this:
		//es.execute(new SpeciesManager("foobar", new HashSet<String>(Arrays.asList("NC_003424.3")), es, listener));

        try{
            FileInputStream fis = new FileInputStream(this.list_species);
            byte[] data = new byte[(int) this.list_species.length()];
            fis.read(data);
            fis.close();
            JSONObject oldSpecies = new JSONObject(new String(data, "UTF-8"));
            
            JSONObject newSpecies = getSpecies(oldSpecies); // pool)
            writeDoneFile(newSpecies.toString(2));
            
        } catch(IOException e) {
            Log.e(e);
        }
    }
    
    private JSONObject getSpecies(JSONObject oldSpecies) throws IOException{ //ExecutorService pool)
        JSONObject newSpecies = new JSONObject();
        HttpURLConnection httpConn = null;
        int responseCode = 0;
        
        try{
            httpConn = (HttpURLConnection) this.url.openConnection();
            responseCode = httpConn.getResponseCode();
        } catch(IOException e) {
            Log.e("No connection to internet, please check your connecion before retry");
            Log.exit();
        } 

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
 
            String currentLine = "";
            String temp = "";
            
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            int endLinePos;
            
            Map<String,Integer> regex = null;
            boolean header = true;
            
            Map specie;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                temp = new String(buffer, 0, bytesRead);
                endLinePos = 0;
                do{
                    endLinePos = temp.indexOf("\n");
                    
                    //We haven't finish the specie/line
                    if(endLinePos == -1)
                        currentLine += temp;
                        
                    else {
                        currentLine += temp.substring(0, endLinePos);
                        if(header) {
                            regex = parseHeader(currentLine);
                            header = false;
                        }
                        else {
                            specie = parseLine(currentLine, regex);
                            if(specie != null &&
                               isToDo(oldSpecies, newSpecies, specie)) {
                               String name = (String) specie.get("name");
                               Set<String> replicons = (Set<String>) specie.get("replicons");
                               
                                /*TODO create a thread fot the specie
                                 * get path and not name
                                 * delete all replicons (one excel file per replicon)
                                 */
                            }
                        }

                        currentLine = "";
                        temp = temp.substring(endLinePos+1, temp.length());
                    }
                    
                }while(endLinePos != -1);
            }
            inputStream.close();
            
            
            Iterator<String> itr = oldSpecies.keys();
            while(itr.hasNext())
                Log.d(itr.next()); //TODO delete the specie, name = itr.next()

        } else {
            Log.e("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        return newSpecies;
    }

    //Create path with group, sub groups, and name of the specie
    private Map parseLine(String line, Map<String,Integer> regex) {
        Map result = new HashMap();
        Set<String> replicons = new HashSet<String>();
        Matcher m = null;
        String[] elements = line.split("\t");
        String elt;
        
        //If the specie has not any replicons
        if(elements.length <= regex.get("replicons") ||
           elements[regex.get("replicons")].trim().isEmpty())
            return null;
        
        
        String[] replicons_brut = elements[regex.get("replicons")].split(";");
        Pattern pattern_replicon = Pattern.compile("^([\\w\\-\\s]*:)?([\\w_\\.]*)(/[\\w_\\.]*)*$");

        for(int i = 0; i < replicons_brut.length; i++) {
            elt = replicons_brut[i].trim();
            m = pattern_replicon.matcher(elt);
            
            if(m.find()) 
                replicons.add(m.group(2));
        }

        result.put("name", elements[regex.get("name")]);
        result.put("modify_date", elements[regex.get("modify_date")]);
        result.put("replicons", replicons);
        
        return result;
    }
    
    //TODO get groups and subgroups
    private Map<String,Integer> parseHeader(String line) {
        String[] elements = line.split("\t");
        String elt;
        Map<String,Integer> regex = new HashMap<String,Integer>();

        for(int i = 0; i < elements.length; i++) {
            elt = elements[i];
            if(elt.contains("Name") || elt.contains("Organism"))
                regex.put("name", i);
            else if(elt.contains("Replicons") || 
                     elt.contains("Segmemts")  || 
                     elt.contains("Segments"))
                regex.put("replicons", i);
            else if(elt.contains("Modify Date"))
                regex.put("modify_date", i);
        }
        
        return regex;
    }
    
    private boolean isToDo(JSONObject oldSpecies, 
                             JSONObject newSpecies, 
                             Map specie) {
                             
        String name = specie.get("name").toString();
        String modify_date = specie.get("modify_date").toString();
        boolean toDo = false;
        
        //If we have already done this specie
        if(newSpecies.has(name))
            return false;
            
        newSpecies.put(name, modify_date);
                    
        if(!oldSpecies.has(name))
            toDo = true;
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            
            try{
                Date oldDate = sdf.parse(oldSpecies.get(name).toString());
                Date currentDate = sdf.parse(modify_date);

            	if(currentDate.compareTo(oldDate) > 0)
                    toDo = true;
            } catch(ParseException e) {}
            
            oldSpecies.remove(name);
        }

        return toDo;
    }
    
    private void writeDoneFile(String text) {
        File done = new File(this.root_path, "done.json");

	    try{
		    PrintWriter writer = new PrintWriter(done.getAbsolutePath(), "UTF-8");
            writer.println(text);
            writer.close();
		} catch(IOException e){
		    Log.e(e);
	    }
	}
}