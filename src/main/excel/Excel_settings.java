package excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

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
	private String[] table;
	private TreeMap<String,Integer>[] old_tab;
	private File f;

	// Constructor
	public Excel_settings (File f, String[] path) throws InvalidFormatException, IOException
	{
		wb = new HSSFWorkbook();
		this.f = f;
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
	public void new_excel (final TreeMap<String,Integer> nucleotide_to_number_1,final TreeMap<String,Integer> nucleotide_to_number_2, final TreeMap<String,Integer> nucleotide_to_number_3) throws IOException
	{
		Log.e("yo");
		f.createNewFile();
		FileOutputStream fileout = new FileOutputStream(f);
		String safename = WorkbookUtil.createSafeSheetName(table[table.length-1]);
		Sheet sheet1 = wb.createSheet(safename);
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

		Row row = sheet1.createRow(0);
		// Name
		row.createCell(0).setCellValue("Nom");
		row.createCell(1).setCellValue(table[table.length-1]);



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
		for (Map.Entry<String, Integer> entry : nucleotide_to_number_1.entrySet())
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
	public void update_excel(final TreeMap<String,Integer> nucleotide_to_number_1, final TreeMap<String,Integer> nucleotide_to_number_2, final TreeMap<String,Integer> nucleotide_to_number_3)
	{
		Log.e("update excel");
	}

	// Current tab
	public TreeMap<String,Integer>[] old_tab()
	{
		return old_tab;
	}

	public static void main (String[] args) throws InvalidFormatException, IOException
	{
		String[] chemin = {"chemin","de","sa","mere"};
		File fil = new File ("/home/mjeremy/stats.xls");
		Excel_settings es = new Excel_settings(fil, chemin);
		TreeMap<String,Integer> test = new TreeMap<String,Integer>();
		test.put("AAA", 100);
		test.put("AAC", 69);
		test.put("IZI", 92);
		es.new_excel(test, null, null);
	}
}
