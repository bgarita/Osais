package logica.utilitarios;

import Mail.Bitacora;
import interfase.otros.ProgressMonitor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;

/**
 * @author: Crysfel Villa Created: Friday, June 03, 2005 4:54:59 PM Modified:
 * Friday, June 03, 2005 4:54:59 PM Modified: Sunday, Sept 08, 2013 6:52:00 PM
 * Bosco Garita
 */
public class Archivos {

    private boolean error;
    private String mensaje_error;
    private final Bitacora b = new Bitacora();

    public boolean isError() {
        return error;
    }

    public String getMensaje_error() {
        return mensaje_error;
    }

    /**
     * Copia un directorio con todo y su contendido
     *
     * @param srcDir
     * @param dstDir
     */
    public void copyDirectory(File srcDir, File dstDir) {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            } // end if

            String[] children = srcDir.list();
            for (String children1 : children) {
                copyDirectory(new File(srcDir, children1), new File(dstDir, children1));
            } // end for
        } else {
            copyFilex(srcDir, dstDir);
        } // end if-else
    } // copyDirectory

    /**
     * Copia un solo archivo
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public void copyFile(File src, File dst) throws IOException {
        OutputStream out;
        try (InputStream in = new FileInputStream(src)) {
            out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            } // end while
            in.close();
        } // end try with resources
        out.close();
    } // end copyFile

    /**
     * Archivos un archivo.
     *
     * @param in
     * @param out
     */
    public void copyFilex(File in, File out) {

        try {
            BufferedOutputStream fileOut;
            try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(in))) {
                fileOut = new BufferedOutputStream(new FileOutputStream(out));
                byte[] buf = new byte[2048];
                int i;
                while ((i = fileIn.read(buf)) != -1) {
                    fileOut.write(buf, 0, i);
                } // end while
                fileIn.close();
                fileOut.close();
            } // end try with resources        
        } catch (Exception ex) {
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }  // end copyFilex

    /*
     Este método todavía no se está usando.  La idea es ir mejorándolo
     de manera que se pueda utilizar para versionar archivos. 14/11/2015
     */
    public boolean compareFiles(File file1, File file2) {
        boolean same = true;
        BufferedInputStream fileC1, fileC2;
        try {
            fileC1 = new BufferedInputStream(new FileInputStream(file1));
            fileC2 = new BufferedInputStream(new FileInputStream(file2));
            byte[] buf1 = new byte[1];
            byte[] buf2 = new byte[1];
            StringBuilder sb1, sb2;
            sb1 = new StringBuilder();
            sb2 = new StringBuilder();
            String str1 = "", str2 = "";
            int i, linea = 1, lineaDif = 0;
            char a, b;
            String ls = System.getProperty("line.separator");
            // Read byte by byte and compare
            while ((i = fileC1.read(buf1)) != -1) {

                fileC2.read(buf2);
                a = (char) buf1[0];
                b = (char) buf2[0];

                // Si el caracter actual es igual al salto de línea...
                if (ls.charAt(0) == a) {
                    String str = "\t" + sb1.toString();
                    System.out.printf("%03d", linea);
                    System.out.printf("%-35s", str);

                    System.out.println();
                    linea++;
                    sb1 = new StringBuilder();
                }

                if (ls.charAt(0) == a) {
                    sb2 = new StringBuilder();
                }

                if (ls.charAt(0) != a) {
                    sb1.append(a);
                }

                if (ls.charAt(0) != b) {
                    sb2.append(b);
                }

                if (a != b && lineaDif == 0) {
                    lineaDif = linea - 1;
                    str1 = sb1.toString();
                    str2 = sb2.toString();
                    same = false;
                }
            } // end while

            if (same) {
                System.out.println("They are equal");
            } else {
                System.out.println("\nLa diferencia se encuentra en la línea: " + lineaDif);
                System.out.println("Observe:");
                System.out.println(str1);
                System.out.println(str2);
            }

            fileC1.close();
            fileC2.close();
        } catch (Exception ex) {
            Logger.getLogger(Archivos.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        return same;
    } // end compareFile

    /**
     * Cuenta la cantidad de archivos y carpetas contenidas en una ruta
     * específica.
     *
     * @author Bosco Garita
     * @param folder
     * @return
     */
    public int countFiles(File folder) {
        int cuenta = 0;

        if (!folder.exists()) {
            return cuenta;
        } // end if

        if (!folder.isDirectory()) {
            return 1;
        } // end if

        String[] children = folder.list();

        for (String f : children) {
            cuenta += countFiles(new File(folder, f));
        } // end for

        return cuenta;
    } // end countFiles

    /**
     * Guardar texto en un archivo ASCII
     *
     * @param text String - texto a almacenar
     * @param path String - nombre del archivo a guardar (incluye la ruta
     * completa)
     * @param append boolean - true=Agrega el texto, false=Reemplaza el texto
     * existente
     * @throws IOException
     * @author Bosco Garita Azofeifa, 13/07/2019
     */
    public void stringToFile(String text, String path, boolean append) throws IOException {
        FileWriter write = new FileWriter(path, append);
        try (PrintWriter pw = new PrintWriter(write)) {
            pw.printf("%s" + "%n", text);
            pw.close();
        } // end try
    } // end stringToFile

    /**
     * Comprime un archivo o una carpeta con todos sus archivos y subcarpeta.
     *
     * @author Bosco Garita Azofeifa
     * @since 20/03/2020
     * @param sourceFile File puede ser un archivo o una carpeta.
     * @param targetFile File debe ser el nombre de un archivo que es donde se
     * guardarán los archivos comprimidos. Si viene nulo el sistema asumirá el
     * mismo nombre que el origen.
     * @param monitor ProgressMonitor muestra el avance y los mensajes como en
     * consola
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void zipFile(File sourceFile, File targetFile, ProgressMonitor monitor) throws FileNotFoundException, IOException {
        FileOutputStream fos;
        String targetFileName;
        if (targetFile == null) {
            targetFileName = sourceFile.getAbsolutePath() + ".zip";
        } else {
            targetFileName = targetFile.getAbsolutePath() + ".zip";
        } // end if-else

        fos = new FileOutputStream(targetFileName);

        ZipOutputStream zos = new ZipOutputStream(fos);

        addZipFile(sourceFile, zos, monitor);

        zos.closeEntry();
        zos.close();
        System.out.println("\nZipped file: " + targetFileName);
    } // end zipFile

    private void addZipFile(File sourceFile, ZipOutputStream zos, ProgressMonitor monitor) throws IOException {
        /*
        Este mensaje aparecerá solo una vez por cada carpeta que se respalde.
        Y si fuera solo un archivo lo que recibe como sourceFile sería ese
        nombre el que se muestre.
         */
        System.out.println("Compressing " + sourceFile.getAbsolutePath());
        // Si el origen es una carpeta, agrego cada uno de los archivos.
        if (sourceFile.isDirectory()) {
            File[] files = sourceFile.listFiles();
            for (File f : files) {
                if (monitor != null) {
                    monitor.setValue(monitor.getValue() + 1);
                } // end if

                // Llamado recursivo
                if (f.isDirectory()) {
                    addZipFile(f, zos, monitor);
                    continue;
                }
                zos.putNextEntry(new ZipEntry(f.getCanonicalPath()));
                byte[] bytes = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
                zos.write(bytes, 0, bytes.length);
            } // end for
        } else {
            if (monitor != null) {
                monitor.setValue(monitor.getValue() + 1);
            } // end if
            zos.putNextEntry(new ZipEntry(sourceFile.getCanonicalPath()));
            byte[] bytes = Files.readAllBytes(Paths.get(sourceFile.getAbsolutePath()));
            zos.write(bytes, 0, bytes.length);
        } // end if
    } // end addZipFile
    
    public void downloadFile(URL url, File target) throws IOException{
        FileUtils.copyURLToFile(url, target);
    } // end downloadFile
    
    public void downloadFile(URL url, String file) throws IOException{
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
    }
} // end class
