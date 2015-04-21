package connection;

import java.util.EventListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.io.File;
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
import java.util.concurrent.ThreadPoolExecutor;


import org.json.JSONObject;
import org.json.JSONArray;
import util.Log;
import util.PathUtils;
import gui.ProgressBarListener;
import core.SpeciesManager;


/**
 * Manages the species
 *
 * Downloads the list of species and determines which have to be updated
 * Create the tree
 * Delete a specie (todo) and update the statistics of his subgroup, group
 * and kingdom
 *
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

	public void parsed(String name) {
    ;
  }

    public void AddSpeciesThreads(ThreadPoolExecutor es,
                                                ProgressBarListener listener){
        try{
            FileInputStream fis = new FileInputStream(this.list_species);
            byte[] data = new byte[(int) this.list_species.length()];
            fis.read(data);
            fis.close();
            JSONObject oldSpecies = new JSONObject(new String(data, "UTF-8"));

            JSONObject newSpecies = getSpecies(oldSpecies, es, listener);
            writeDoneFile(newSpecies.toString(2));

        } catch(IOException e) {
            Log.e(e);
        }
    }

    private JSONObject getSpecies(JSONObject oldSpecies, ThreadPoolExecutor es,
                              ProgressBarListener listener) throws IOException{

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
                            specie = parseIndividual(currentLine, regex);
                            if(specie != null &&
                               isToDo(oldSpecies, newSpecies, specie)) {

                               SpeciesManager sm = new SpeciesManager(this.root_path,
                                                                      specie,
                                                                      es, listener);

                               es.execute(sm);
                            }
                        }

                        currentLine = "";
                        temp = temp.substring(endLinePos+1, temp.length());
                    }

                }while(endLinePos != -1);
            }
            inputStream.close();

            JSONObject specie_to_remove;
            String name;
            Iterator<String> itr = oldSpecies.keys();

            while(itr.hasNext()) {
                name = itr.next();
                specie_to_remove = (JSONObject) oldSpecies.get(name);

                /* TODO delete the specie and modify stats on all the path
                 * i.e: kingdom, group and subgroup stats
                 *
                 * Dir kingdom: this.root_path                          (File)
                 * group = (String)specie_to_remove.get("Group")        (String)
                 * subGroup = (String)specie_to_remove.get("SubGroup")  (String)
                 * name = name                                          (String)
                 *
                 * to append a String at the File : new File(File, String)
                 * to append many String at a File: util.PathUtils.join(File, String...)
                 *
                 * if you have a question -> nicolas ;)
                 */
            }

        } else {
            Log.e("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        return newSpecies;
    }


    //Function to parse a specie of the species list's file
    private Map parseIndividual(String line, Map<String,Integer> regex) {
        Map result = new HashMap();
        Set<String> replicons = new HashSet<String>();
        Matcher m = null;
        String[] elements = line.split("\t");
        String elt;

        //If the specie has any replicons
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

        for(Entry<String, Integer> entry : regex.entrySet())
            result.put(entry.getKey(), elements[entry.getValue()]);

        result.put("replicons", replicons);

        return result;
    }

    //Function to parse the header of the species list file
    private Map<String,Integer> parseHeader(String line) {
        String[] elements = line.split("\t");
        String elt;
        Map<String,Integer> regex = new HashMap<String,Integer>();

        for(int i = 0; i < elements.length; i++) {
            elt = elements[i];
            if(elt.contains("Name") || elt.contains("Organism"))
                regex.put("name", i);
            else if(elt.equals("Replicons") ||
                     elt.equals("Segmemts")  ||
                     elt.equals("Segments"))
                regex.put("replicons", i);
            else if(elt.equals("Modify Date"))
                regex.put("modify_date", i);
            else if(elt.equals("Group"))
                regex.put("group", i);
            else if(elt.equals("SubGroup"))
                regex.put("subGroup", i);
        }

        return regex;
    }


    /* Function to check if a specie is to update
     *
     *Side effects:
     * add the specie to the new updated list
     * remove it from the old one (the species which is still in at the end
     * must be remove )
     */
    private boolean isToDo(JSONObject oldSpecies,
                             JSONObject newSpecies,
                             Map specie) {

        String name = specie.get("name").toString();
        String modify_date = specie.get("modify_date").toString();
        String group= specie.get("group").toString();
        String subGroup = specie.get("subGroup").toString();

        boolean toDo = false;

        Map<String,String> specie_saved = new HashMap<String,String>();
        JSONObject old_specie;

        //If we have already done this specie
        if(newSpecies.has(name))
            return false;

        specie_saved.put("modify_date", modify_date);
        specie_saved.put("group", group);
        specie_saved.put("subGroup", subGroup);

        newSpecies.put(name, specie_saved);

        File path_specie = PathUtils.child(this.root_path,
                                          (String)specie.get("group"),
                                          (String)specie.get("subGroup"),
                                          name);

        if(!oldSpecies.has(name)) {
            toDo = true;
						SpeciesManager.toDo(this.root_path, specie);
            path_specie.mkdirs(); //Create the specie path
        }
        else if(!path_specie.exists()) {
            Log.e("The specie path " + path_specie.getAbsolutePath() +
                  " doesn't exist anymore. However, we have already parse it");
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            old_specie = (JSONObject) oldSpecies.get(name);
            try{
                Date oldDate = sdf.parse((String)old_specie.get("modify_date"));
                Date currentDate = sdf.parse(modify_date);

							//If it's a new one or
							//If we stoped before to parse the sepcie
							//do it again.
            	if(currentDate.compareTo(oldDate) > 0 || !SpeciesManager.isDone(this.root_path , specie)) {
								SpeciesManager.toDo(this.root_path, specie);
                toDo = true;
							}
            } catch(ParseException e) {}

            oldSpecies.remove(name);
        }

        return toDo;
    }

    //function to write the text in the species list file
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
