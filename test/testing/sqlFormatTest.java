package testing;

import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, 04/05/2025
 */
public class sqlFormatTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String item1 = "art1";
        String item2 = "art2";
        String sqlSent = "call ejecutarCierre(" + item1 + "," + item2 + ")";
        
        System.out.println(Ut.sqlFormat(sqlSent));
    }
    
}
