
package testing;

import java.io.File;
import java.io.IOException;
import logica.utilitarios.Archivos;

/**
 *
 * @author bgari
 */
public class TestZip {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        /*
        String file[] = {"/Users/bgari/OneDrive/Documentos/Español.odt"};
        ZipFile.main(file);
        */
        
        //File source = new File("/Apologética"); // Respalda toda la carpeta y sus subcarpetas
        File source = new File("/Apologética/Documentos/Sola Fe.pdf"); // Respalda solo este archivo
        File target = new File("/Backups/Apologética");
        
        // Falta seguir probando
        Archivos archivos = new Archivos();
        archivos.zipFile(source, target, null);
        
    }
    
}
