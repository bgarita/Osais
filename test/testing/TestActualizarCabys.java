
package testing;

import accesoDatos.UtilBD;

/**
 *
 * @author bgari
 */
public class TestActualizarCabys {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws Exception {
        String msg = UtilBD.actualizarCabys(null, null, null);
        System.out.println(msg);
    }
    
}
