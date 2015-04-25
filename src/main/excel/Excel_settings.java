package excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;

import util.Log;

/**
 * Class to manage the Excel (create/update).
 * @author mjeremy
 * @since 1.0
 *
 */
public class Excel_settings {
	// Attributes
	private Workbook wb;
	private ArrayList<String> table;
	private TreeMap<String,Integer>[] old_tab;
	private File f;
	private String f_name;

	// Constructor
	public Excel_settings (File f, ArrayList<String> path) throws InvalidFormatException, IOException
	{
		wb = new HSSFWorkbook();
		this.f = f;
		this.f_name = f.getName();
		table = path;
	}

	// Create
	/**
	 * Create a new xls with the number of each nucleotide.
	 * @param nucleotide_to_number_1
	 * 		Number of nucleotide in phase 0.
	 * @param nucleotide_to_number_2
	 * 		Number of nucleotide in phase 1.
	 * @param nucleotide_to_number_3
	 * 		Number of nucleotide in phase 2.
	 * @throws IOException
	 */
	private void new_excel (final List<TreeMap<String,Integer>> diff) throws IOException{
		f.createNewFile();
		FileOutputStream fileout = new FileOutputStream(f);
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
		row.createCell(0).setCellValue("Nb CDS traités");
		Cell cell = row.createCell(1);
		cell.setCellStyle(cellStyle);

		row = sheet1.createRow(3);
		row.createCell(0).setCellValue("Nb Trinucléotides");
		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);

		row = sheet1.createRow(4);
		row.createCell(0).setCellValue("Nb CDS non traités");
		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);
		// --------------------------------------------------


		// Trinucléotides
		// -----------------------------------------------------------------------
		row = sheet1.createRow(6);
		cell = row.createCell(0);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Trinucléotides");
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

		for(i = 0 ; i < 7 ; i++)
			sheet1.autoSizeColumn(i,true);
		wb.write(fileout);
		fileout.close();
	}

	// Update
	/**
	 * Update the old xls with new number of each nucleotide.
	 * @param nucleotide_to_number_1
	 * 		Number of nucleotide in phase 0.
	 * @param nucleotide_to_number_2
	 * 		Number of nucleotide in phase 1.
	 * @param nucleotide_to_number_3
	 * 		Number of nucleotide in phase 2.
	 */
	private void update_excel(final List<TreeMap<String,Integer>> diff)
	{
		//Must be thread safe ;)
		;
	}

	// Current tab
	private TreeMap<String,Integer>[] old_tab()
	{
		return old_tab;
	}

	//Okay, not a very fun name aha
	public static void update_helper(Excel_settings es,
												 final TreeMap<String,Integer> nucleotide_to_number_1,
								  			 final TreeMap<String,Integer> nucleotide_to_number_2,
												 final TreeMap<String,Integer> nucleotide_to_number_3) throws IOException, InvalidFormatException
	{

		List<TreeMap<String,Integer>> diff = new ArrayList<TreeMap<String,Integer>>();

		diff.add(nucleotide_to_number_1);
		diff.add(nucleotide_to_number_2);
		diff.add(nucleotide_to_number_3);

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

		update_helper_aux(es, diff);
	}

	public static void update_helper_aux(Excel_settings es, List<TreeMap<String,Integer>> diff) throws IOException, InvalidFormatException
	{
		////// Do the action
		if(es.f.exists())
			es.update_excel(diff);
		else
			es.new_excel(diff);

		//Stop the recurssion if it's the kingom root
		//0 means, tree directory, I think it's better to concatanate the three kingdom after
		//Otherwise all the threads will modify it, a bit tricky I guess.
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

		update_helper_aux(es_parent, diff);
	}

	public static void main (String[] args) throws InvalidFormatException, IOException
	{
		ArrayList<String> chemin = new ArrayList<String>();
		chemin.add("Viruses");
		chemin.add("dsRNA viruses");

		File fil = new File ("/home/rinku/Workspaces/Cours/bio_info/Excel-Lent/tree/Viruses/dsRNA viruses/test.xls");
		Excel_settings es = new Excel_settings(fil, chemin);
		TreeMap<String,Integer> test = new TreeMap<String,Integer>();

		test.put("AAA", 100);
		test.put("AAC", 69);
		test.put("IZI", 92);

		update_helper(es, test, null, null);
	}
}
