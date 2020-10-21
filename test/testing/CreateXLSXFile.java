package testing;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author bgari
 */
public class CreateXLSXFile {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File file = new File("/temp/Geeks.xlsx");
        Workbook wb = new HSSFWorkbook(); 
        OutputStream out = new FileOutputStream(file); 
        
        // Creating a sheet using predefined class provided by Apache POI 
        Sheet sheet = wb.createSheet("Company Prepration"); 
        
        // Creating a row at specific position 
        // using predefined class provided by Apache POI 
  
        // Specific row number 
        Row row = sheet.createRow(1); 
        
        // Specific cell number 
        Cell cell = row.createCell(1); 
        
        // putting value at specific position 
        cell.setCellValue("Geeks"); 
        
        // writing the content to Workbook 
        wb.write(out); 
        out.close();
  
        System.out.println("given cell is created at position (1, 1)");
        Desktop.getDesktop().open(file);
    } // end main
    
} // end class
