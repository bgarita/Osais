package testing;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

/**
 * This Java program demonstrates how to compress a file in ZIP format.
 *
 * @author www.codejava.net
 */
public class ZipFile {

    private static void zipFile(String filePath) {
        try {
            File file = new File(filePath);
            String zipFileName = file.getName().concat(".zip");
 
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
 
            zos.putNextEntry(new ZipEntry(file.getName()));
 
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
            zos.close();
            System.out.println("Zipped file: " + zipFileName);
        } catch (FileNotFoundException ex) {
            System.err.format("The file %s does not exist", filePath);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String filePath = args[0];
        zipFile(filePath);
    }
    
}