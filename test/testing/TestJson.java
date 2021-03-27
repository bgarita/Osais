package testing;

import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

/**
 *
 * @author bgari
 */
public class TestJson {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        JSONObject json = new JSONObject();

        // Cadenas de texto básicas
        json.put("name", "Bosco");
        json.put("last name", "Azofeifa");
        
        
        System.out.print(json.toString());
        
        FileWriter file = new FileWriter("TestJson.js");
        file.write(json.toString());
        file.close();
    }
    
}
