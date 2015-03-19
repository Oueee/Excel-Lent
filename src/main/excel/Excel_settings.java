package excel;

import java.util.Hashtable;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

public class Excel_settings {
	// Constants
	private static final String _default_name = "stats.xls";
	private static final String windows_delimiter = "\\";
	private static final String linux_delimiter = "/";
	private static final boolean is_windows = System.getProperty("os.name").startsWith("Windows");
	private static final String[] list_arbo = {"Kingdom","Group","Subgroup","Organism","Name"};
	
	// Attributes
	private Workbook wb;
	private Map<String,String> table;
	
	// Constructors
	public Excel_settings (String path){
		wb = new HSSFWorkbook();
		final String os_delimiter = (is_windows) ? windows_delimiter : linux_delimiter; 
		final String [] treeborescence = path.split(os_delimiter);
		table = new Hashtable<String,String>();
		
		for (int i=0 ; i<treeborescence.length ; i++)
			table.put(list_arbo[i], treeborescence[i]);
	}

}
