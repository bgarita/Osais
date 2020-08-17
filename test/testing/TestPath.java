package testing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author bgarita
 */
public class TestPath {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String fileName = "C:\\Java Programs\\osais\\log.txt";
            Path path = Paths.get(fileName);
            Stream<String> stream = Files.lines(path, Charset.forName("UTF-8"));
            Object[] content = stream.toArray();
            for (Object o : content) {
                System.out.println(o);
            } // end for
        } catch (IOException ex) {
            Logger.getLogger(TestPath.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
