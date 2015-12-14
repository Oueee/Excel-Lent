package excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Array;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.json.JSONObject;
import org.json.JSONArray;
import util.Log;
import util.PathUtils;
import core.ExcelLent;
import statistics.AnalysisResults;
import gui.ProgressBarListener;

/**
 * Class to manage the Excel (create/update).
 * @author mjeremy
 * @since 1.0
 *
 */
public class Excel_settings {
	// Attributes

	public static String extension = ".xls";
  
  //Why these here? I don't know, I don't know... pa pala pa pa
  private static final String types_replicons[] = {"chromosome", "mitochondrion", 
                                                     "chloroplast", "plasmid", "plastid", 
                                                     "linkage", "macronuclear", "DNA", "RNA"};
	/**
	 * The current Workbook.
	 * @see Excel_settings#Excel_settings(File, ArrayList)
	 * @see Excel_settings#new_excel(List)
	 * @see Excel_settings#update_excel(List)
	 * @see Excel_settings#fill_excel(List, Sheet)
	 */
	private Workbook wb;

	/**
	 * Path of the trinucleotides.
	 * @see Excel_settings#Excel_settings(File, ArrayList)
	 * @see Excel_settings#new_excel(List)
	 * @see Excel_settings#update_excel(List)
	 * @see Excel_settings#update_helper_aux(Excel_settings, List)
	 */
	private ArrayList<String> table;

	/**
	 * Used to compare new and old numbers of trinucleotides.
	 * @see Excel_settings#old_tab()
	 * @see Excel_settings#update_helper(Excel_settings, TreeMap, TreeMap, TreeMap)
	 */
	private TreeMap<String,Integer>[] old_tab;

	/**
	 * Current file.
	 * @see Excel_settings#Excel_settings(File, ArrayList)
	 * @see Excel_settings#new_excel(List)
	 * @see Excel_settings#update_excel(List)
	 * @see Excel_settings#update_helper(Excel_settings, TreeMap, TreeMap, TreeMap)
	 * @see Excel_settings#update_helper_aux(Excel_settings, List)
	 */
	private File f;

	private FileLock lock;

	/**
	 * Name of the current file.
	 * @see Excel_settings#update_helper_aux(Excel_settings, List)
	 */
	private String f_name;

	// Constructor
	public Excel_settings (File f, ArrayList<String> path) throws InvalidFormatException, IOException
	{
		wb = new HSSFWorkbook();
		this.f = f;
		this.f_name = f.getName();
		table = path;
	}
  
  public static void agregate_excels(boolean fine) throws InvalidFormatException, IOException{
    agregate_excels(fine, null);
	}
	
		//Functions launched after all the epeces done.
	//It agregate the leafs stats in node stats.
	public static void agregate_excels(boolean fine, ProgressBarListener listener) throws InvalidFormatException, IOException{
    
    for(String type_replicon : types_replicons) {
      //console case
      if(listener != null)
        listener.setText("Create statistics for " + type_replicon);
      agregate_aux(fine, new Excel_settings(ExcelLent.tree_root, new ArrayList<String>()), type_replicon);
    }
	}
                      
	public Box get_infos() {
		Sheet sheet1 = wb.getSheetAt(0);

		List<TreeMap<String,Integer>> list = new ArrayList<TreeMap<String,Integer>> (6);
  
		long nbcds = (long)sheet1.getRow(2).getCell(1).getNumericCellValue();
		long nbcds_no = (long)sheet1.getRow(4).getCell(1).getNumericCellValue();
		long trinucle = (long)sheet1.getRow(3).getCell(1).getNumericCellValue();

		for (int k=1 ; k < 9 ;)
		{
			TreeMap<String, Integer> m = new TreeMap<String,Integer>();
			for (int i = 7 ; i < AnalysisResults.CDS_STRINGS.length + 7 ; i++)
			{
				double nb = sheet1.getRow(i).getCell(k).getNumericCellValue();
				m.put(AnalysisResults.CDS_STRINGS[i-7], (int)nb);
			}
			list.add(m);
			if (k<6)
				k+=2;
			else
				k+=1;
		}

		return new Box(list,nbcds,nbcds_no,trinucle);
	}
    

	public static Box agregate_aux(boolean fine, Excel_settings es, String type) throws InvalidFormatException, IOException {
	    Box result = null;
	    //Type represent the type of the stats we want to agregate (chromosome, mitochondrie...)
		if(es.f.isFile()) {
		    if(es.f.getName().equals(type + extension)) { // If it's a good stat file, we get the informations
          try{
			      es.wb = WorkbookFactory.create(es.f);
			      result = es.get_infos();
			      es.wb.close();
			    }catch(Exception e){
			      Log.e("excel file " + es.f + " is malformed!");
			    }
			  }
		}
		else {
		    Box b = new Box();
            File[] children = es.f.listFiles();
            boolean leaf = true;
            
            //Test if it's a leaf (id: a replicon)
            if(children == null)
              return result;
              
		    for (File file : children) {
		        try{//Bug once, don't know why
		          if(!file.isFile())
		              leaf = false;
		        }catch(Exception e) {Log.d(file);}
		        
		    }
		    
		    //Get the stat file in the current dir (There is only one here)
		    //And return it
		    //TODO : check if it's empty
		    if(leaf) {
		      if(Array.getLength(children) > 0) {
		        for(File file : children) { // Actually, it can be a block file if we stop the program during the writing
		          ArrayList<String> path = es.table;
			        path.add(file.getName());
			        result = agregate_aux(fine, new Excel_settings(file, path), type);
			      }
			    }
		    }
		    else {
		        //Else we create a new one agregated and propagate
		        //For the fine stats, we just get the first bio project (path size 5)
		        boolean first = true;
		        for (File file : children) {
		          if(!file.isFile()) {
		             // If it's the specie folder and it's a fine stat and it's not the first one, we pass
		            if(!fine || first || es.table.size() != 4)
		            {
		              ArrayList<String> path = new ArrayList<String>();
		              for(String path_elt : es.table) //Deep copy
		                  path.add(path_elt);
			            path.add(file.getName());

			            Box b_son = agregate_aux(fine, new Excel_settings(file, path), type);
			            b = b.add(b_son);
			            first = false;
			           }
			        }
		        }
		
		        //Do not the root
		        if(es.f.getName().equals(ExcelLent.tree_root.getName()))
			        return null;

                //After that we delete the file if it's exist
                //Indeed, it can be empty thus we can't update it
		        es.f = util.PathUtils.child(es.f, type + extension);

		        if(es.f.exists())
			        es.f.delete();
                
		        if(!b.isEmpty()) {
		            //Then create a new one
                    es.f.createNewFile();
                    //And fill it
		            FileOutputStream fileout = new FileOutputStream(es.f);
		            es.new_excel(b.l,b.nCds, b.nCdsNot, b.nbNucleotides);
		            es.wb.write(fileout);
		            fileout.close();
		        }
		        result = b;
		    }
        }
		return result;
	}
	
	// Create
	/**
	 * Create a new xls with the number of each nucleotide.
	 * @param diff
	 * 		The number of the trinucleotide phases.
	 * @param nb_cds
	 * 		The number of treated CDS.
	 * @param nb_cds_nt_treat
	 * 		The number of untreated CDS.
	 * @throws IOException
	 * @see Excel_settings#update_helper_aux(Excel_settings, List)
	 */
	private void new_excel (final List<TreeMap<String,Integer>> diff,final long nb_cds, final long nb_cds_nt_treat, final long nb_tr) throws IOException{
		String name_element = table.get(table.size()-1);
		String safename = WorkbookUtil.createSafeSheetName(name_element);
		Sheet sheet1 = wb.createSheet(safename);
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

		Row row = sheet1.createRow(0);

		// Name
		row.createCell(0).setCellValue("Nom");
		row.createCell(1).setCellValue(name_element);

		row = sheet1.createRow(1);
		row.createCell(0).setCellValue("Chemin");
		// for each element in the path
		int i = 1;
		for (String p : table)
			row.createCell(i++).setCellValue(p);

		// CDS/Trinucleotides NB
		// --------------------------------------------------
		row = sheet1.createRow(2);
		row.createCell(0).setCellValue("Nb CDS traites");
		Cell cell = row.createCell(1);
		cell.setCellStyle(cellStyle);

		row = sheet1.createRow(3);
		row.createCell(0).setCellValue("Nb Trinucleotides");
		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);

		row = sheet1.createRow(4);
		row.createCell(0).setCellValue("Nb CDS non traites");
		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);
		// --------------------------------------------------


		// Trinucléotides
		// -----------------------------------------------------------------------
		row = sheet1.createRow(6);
		cell = row.createCell(0);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Trinucleotides");
		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Nb Ph0");
		cell = row.createCell(2);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Pb Ph0");
		cell = row.createCell(3);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Nb Ph1");
		cell = row.createCell(4);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Pb Ph1");
		cell = row.createCell(5);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Nb Ph2");
		cell = row.createCell(6);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Pb Ph2");
		cell = row.createCell(7);
		cell.setCellValue("Pr Ph0");
		cell.setCellStyle(cellStyle);
		cell = row.createCell(8);
		cell.setCellValue("Pr Ph1");
		cell.setCellStyle(cellStyle);
		cell = row.createCell(9);
		cell.setCellValue("Pr Ph2");
		cell.setCellStyle(cellStyle);

		// Key of map
		i = 7;
		for (Map.Entry<String, Integer> entry : diff.get(0).entrySet())
		{
			String key = entry.getKey();
			row = sheet1.createRow(i++);
			cell = row.createCell(0);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(key);
		}
		Font font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle style = wb.createCellStyle();
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		row = sheet1.createRow(i);
		cell = row.createCell(0);
		cell.setCellStyle(style);
		cell.setCellValue("Total");
		// -----------------------------------------------------------------------
    fill_excel(diff,sheet1,nb_cds,nb_cds_nt_treat,nb_tr);
	}

	private FileChannel lock(FileOutputStream fileout) throws IOException, InterruptedException {
		FileChannel fchannel = null;

		fchannel = fileout.getChannel();
		while ((this.lock = fchannel.tryLock(0, Long.MAX_VALUE, false)) == null)
			Thread.sleep(1000); // wait the disponibility of the file

		return fchannel;
	}

	private void release(FileChannel fchannel) throws IOException {
		//this.lock.release();
		//release block, doesn't work ?
		//unlock when the channel is close (c.f the doc)
		fchannel.close();
	}


	// Update
	/**
	 * Update the old xls with new number of each nucleotide.
	 * @param diff
	 * 		Difference between the 3 new and old trunicleotide phases.
	 * @param nb_cds
	 * 		The number of treated CDS.
	 * @param nb_cds_nt_treat
	 * 		The number of untreated CDS.
	 * @throws IOException
	 * @throws InterruptedException
	 * @see Excel_settings#update_helper_aux(Excel_settings, List)
	 */
	private void update_excel(final List<TreeMap<String,Integer>> diff,final long nb_cds, final long nb_cds_nt_treat, final long nb_tr) throws IOException, InterruptedException
	{
		Sheet sheet1 = wb.getSheetAt(0);
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

		fill_excel(diff,sheet1,nb_cds,nb_cds_nt_treat, nb_tr);
	}

	/**
	 * Allow to fill the excel file either a creating either an update.
	 * @param value
	 * 		Value to write it.
	 * @return
	 * 		Trinucleotides treated.
	 * @param nb_cds
	 * 		The number of treated CDS.
	 * @param nb_cds_nt_treat
	 * 		The number of untreated CDS.
	 * @see Excel_settings#new_excel(List, int, int)
	 * @see Excel_settings#update_excel(List, int, int)
	 */
	private void fill_excel(final List<TreeMap<String, Integer>> value, Sheet sheet1,
			final long nb_cds, final long nb_cds_nt_treat, final long nb_tr )
	{
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    
		CellStyle cellStyle_percentage = wb.createCellStyle();
		DataFormat format = wb.createDataFormat();
		cellStyle_percentage.setDataFormat(format.getFormat("0.00"));
		cellStyle_percentage.setAlignment(CellStyle.ALIGN_CENTER);
		
		final int min = 7; // row min
		final int max = 70; // row max

		int j = 1; // current column

		int phase = 0;
		final String[] phases = {"B","D","F","H","I","J"}; // count phases

		// for the 3 phases
		List<TreeMap<String,Integer>> val = value.subList(0, 3);
		for (TreeMap<String,Integer> tree : val)
		{
			int current = 0 + min;
			for (Map.Entry<String, Integer> entry : tree.entrySet())
			{ // for each trinucleotides
				//Log.d(entry.getValue());
				Row row = sheet1.getRow(current++);
				
				//Put count
				Cell cell = row.createCell(j);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(entry.getValue());
				
				//Put percentage
				Cell cell_p = row.createCell(j+1);
				cell_p.setCellStyle(cellStyle_percentage);
				cell_p.setCellValue(entry.getValue() / (double)nb_tr);
			}

			Row row = sheet1.getRow(max+1);
			Cell cell = row.createCell(j);
			String letter = phases[phase++];
			cell.setCellStyle(cellStyle);
			// sum of the column count
			cell.setCellFormula("SUM("+ letter +"8:"+ letter +"71)");
			j+=2;
		}
		val = value.subList(3, 6);
		// for the three phases' pr
		for (TreeMap<String,Integer> tree : val)
		{
			int current = 0 + min;
			for (Map.Entry<String, Integer> entry : tree.entrySet())
			{ // for each trinucleotides
				//Log.d(entry.getValue());
				Row row = sheet1.getRow(current++);
				
				//Put count
				Cell cell = row.createCell(j);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(entry.getValue());
			}
			j++;
		}

	  /*
		for (int i = 0,cur=2 ; i < 3 && cur<7 ; i++,cur+=2)
		{
			String letter = phases[i];
			for (int k = 7 ; k < 72 ; k++)
			{
				Row row = sheet1.getRow(k);
				Cell cell = row.createCell(cur);
				cell.setCellStyle(cellStyle);
				// percentage
				cell.setCellFormula(letter + (k+1) + "/" + letter + "72");
			}
		}
	    */
	    Row row;
	    Cell cell;
	    row = sheet1.getRow(2);
		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(nb_cds);

		
		row = sheet1.getRow(3);
		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(nb_tr);

		row = sheet1.getRow(4);
		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(nb_cds_nt_treat);
		
				// not really effecient...
		for(int i = 0 ; i < 10 ; i++)
			sheet1.autoSizeColumn(i,true);
			
	}

	/**
	 * Get the old trinucleotide phases.
	 * @return attribute <b>old_tab</b>
	 */
	private TreeMap<String,Integer>[] old_tab()
	{
		return old_tab;
	}

	//Okay, not a very fun name aha
	/**
	 * List the difference to do in each file in the path.<br>
	 * (for example in <b>SubGroup</b>, update [<b>Group</b>,<b>KingDom</b>])
	 * @param es
	 * 		The xls file to update.
	 * @param nucleotide_to_number_1
	 * 		Number of trinucleotide in phase 0.
	 * @param nucleotide_to_number_2
	 * 		Number of trinucleotide in phase 1.
	 * @param nucleotide_to_number_3
	 * 		Number of trinucleotide in phase 2.
	 * @param nb_cds
	 * 		Number of treated CDS.
	 * @param nb_cds_untreated
	 * 		Number of not treated CDS.
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws InterruptedException
	 */
	public static void update_helper(Excel_settings es,
									final TreeMap<String,Integer> nucleotide_to_number_1,
									final TreeMap<String,Integer> nucleotide_to_number_2,
									final TreeMap<String,Integer> nucleotide_to_number_3,
									final int nb_cds,
									final int nb_cds_untreated) throws IOException, InvalidFormatException, InterruptedException
	{

		List<TreeMap<String,Integer>> diff = new ArrayList<TreeMap<String,Integer>>();

		diff.add(nucleotide_to_number_1);
		diff.add(nucleotide_to_number_2);
		diff.add(nucleotide_to_number_3);
		
		TreeMap<String,Integer> pr1 = new TreeMap<String,Integer>();
		TreeMap<String,Integer> pr2 = new TreeMap<String,Integer>();
		TreeMap<String,Integer> pr3 = new TreeMap<String,Integer>();
		
		int nb_tr = 0;
		for (Map.Entry<String, Integer> entry : nucleotide_to_number_1.entrySet())
		{
			nb_tr += entry.getValue();
			int nbp1 = entry.getValue();
			int nbp2 = nucleotide_to_number_2.get(entry.getKey());
			int nbp3 = nucleotide_to_number_3.get(entry.getKey());
			boolean[] t = new boolean[3];
			t[0]= nbp1>=nbp2 && nbp1>=nbp3; // if it is Pr
			t[1]= nbp2>=nbp1 && nbp2>=nbp3;
			t[2]= nbp3>=nbp1 && nbp3>=nbp2;
			pr1.put(entry.getKey(), (t[0])?1:0);
			pr2.put(entry.getKey(), (t[1])?1:0);
			pr3.put(entry.getKey(), (t[2])?1:0);
		}
		
		diff.add(pr1);
		diff.add(pr2);
		diff.add(pr3);

		//If it's an update (and not a new excel file)
		//We check the difference, otherwise the difference is just the new.
		//because the difference btw 0 and a number is the number
		if(es.f.exists()) {
			TreeMap<String,Integer>[] old_tabs = es.old_tab();
			TreeMap<String,Integer> old_tab;
			TreeMap<String,Integer> diff_tab;

			for(int i = 0; i < 3; i++) {
				old_tab = old_tabs[i];
				diff_tab = diff.get(i);
			//diff = new - old
			//if new is more than old, diff is positive (the difference btw then
			//The same if new is less, but diff is obviously negative)
			for (Map.Entry<String, Integer> entry : old_tab.entrySet())
				diff_tab.put(entry.getKey(), diff_tab.get(entry.getKey()) - entry.getValue());
			}
		}

		update_helper_aux(es, diff,nb_cds,nb_cds_untreated, nb_tr);
	}

	/**
	 * Auxiliary function used to iterate on the file in the path.
	 * @param es
	 * 		File to update.
	 * @param diff
	 * 		Difference in the number of trinucleotides.
	 * @param nb_cds
	 * 		Number of treated CDS.
	 * @param nb_cds_untreated
	 * 		Number of not treated CDS.
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws InterruptedException
	 * @see Excel_settings#update_helper(Excel_settings, TreeMap, TreeMap, TreeMap)
	 */
	public static void update_helper_aux(Excel_settings es, List<TreeMap<String,Integer>> diff,
										final int nb_cds,
										final int nb_cds_untreated,
										final int nb_tr) throws IOException, InvalidFormatException, InterruptedException
	{

		boolean exist = true;

		if(!es.f.exists()) {
			exist = false;
			es.f.createNewFile();
		}

		FileOutputStream fileout = new FileOutputStream(es.f);

		/////////////////
		//// Secured part start
		//FileChannel fchannel = es.lock(fileout);

			////// Do the action
			if(exist)
				es.update_excel(diff,nb_cds,nb_cds_untreated,nb_tr);
			else
				es.new_excel(diff,nb_cds,nb_cds_untreated,nb_tr);

			es.wb.write(fileout);
			fileout.close();
			es.wb.close();
		//es.release(fchannel);
		//// Secured part ended
		/////////////////

		//Stop the recursion if it's the kingdom root
		//0 means, tree directory, I think it's better to concatenate the three kingdom after
		//Otherwise all the threads will modify it, a bit tricky I guess.

		return;

		/*
		if(es.table.size() <= 1)
			return;

		////// Up one level
		ArrayList<String> table_parent = new ArrayList<String>();

		for(String p : es.table)
			table_parent.add(p);

		table_parent.remove(table_parent.size() - 1);
		File f_parent = es.f.getParentFile().getParentFile();
		f_parent = new File(f_parent, es.f_name);

		Excel_settings es_parent = new Excel_settings(f_parent, table_parent);

		update_helper_aux(es_parent, diff,nb_cds,nb_cds_untreated);
		*/
	}

	public static void main (String[] args) throws InvalidFormatException, IOException, InterruptedException
	{
		/*
		ArrayList<String> chemin = new ArrayList<String>();
		chemin.add("Viruses");
		chemin.add("dsRNA viruses");

		File fil = new File ("/home/rinku/Workspaces/Cours/bio_info/Excel-Lent/tree/Viruses/dsRNA viruses/test.xls");
		Excel_settings es = new Excel_settings(fil, chemin);
		TreeMap<String,Integer> test = new TreeMap<String,Integer>();

		test.put("AAA", 100);
		test.put("AAC", 69);
		test.put("IZI", 92);

		update_helper(es, test, null, null,0,0);
		*/
		agregate_excels(false);
	}
}
