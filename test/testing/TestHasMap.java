/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bosco
 */
public class TestHasMap {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Map m = new HashMap();
        String clicode = "clicode";
        String clidesc = "clidesc";
        
        m.put(clicode, 10);
        m.put(clidesc, "prueba");

        System.out.print(m.get(clicode));
        System.out.println(" " + m.get(clidesc));
        
    }

}
