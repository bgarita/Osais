package testing;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bosco
 */
public class TestLogger {

    /**
     * @param args the command line arguments
     * Crear un logger que guarde en un archivo de texto 
     * con formato xml.
     */
    public static void main(String[] args) throws IOException {
        Logger l;
        l = Logger.getLogger(TestLogger.class.getName());
        l.setLevel(Level.SEVERE);
        FileHandler f = new FileHandler("Log.xml");
        
        l.addHandler(f);
        
        l.log(Level.SEVERE, "Otra prueba de logging");
        
    }
}
