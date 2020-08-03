package testing;

import java.util.UUID;
import logica.utilitarios.DirectoryStructure;

/**
 *
 * @author bgari
 */
public class TestDirectoryStructure {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DirectoryStructure d = new DirectoryStructure();
        //System.out.println(d);
        System.out.println(d.getXmls());
        //UUID gfg = UUID.randomUUID();
        System.out.println(UUID.randomUUID().toString());
    }
    
}
