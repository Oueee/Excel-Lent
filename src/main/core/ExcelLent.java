package main.core;

import main.util.Log;
import main.connection.GenomeManager;


import java.io.File;


public class ExcelLent {
    private static GenomeManager virusesManager;
    private static GenomeManager eukaryotesManager;
    private static GenomeManager prokaryotesManager;
    

	public static void main(String[] args) {
	    File project_root = new File(System.getProperty("user.dir"));
	    File tree_root = new File(project_root.getParent(), "tree");
        

        Log.i("Initializing the three genome manager");
        
        virusesManager = new GenomeManager(new File(tree_root, "Viruses"));
        eukaryotesManager = new GenomeManager(new File(tree_root, "Eukaryotes"));
        prokaryotesManager = new GenomeManager(new File(tree_root, "Prokaryote"));
        
        Log.i("Getting the lists and checking what to update");
        virusesManager.AddSpeciesThreads();
        eukaryotesManager.AddSpeciesThreads();
        prokaryotesManager.AddSpeciesThreads();
	}
}