package testing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import logica.utilitarios.Archivos;

/**
 *
 * @author bosco
 */
public class WriteTextFiles {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        /*
        try (PrintWriter writer = new PrintWriter("bosco.txt", "UTF-8")) {
            writer.println("The first line");
            writer.println("The second line");
        }
        */
        Archivos archivo = new Archivos();
        String text = "[client]";
        String path = "bosco.txt";
        boolean append = true;
        
        archivo.stringToFile(text, path, append);
        
        text = "bendición";
        archivo.stringToFile(text, path, append);
    } // end main
    
}
