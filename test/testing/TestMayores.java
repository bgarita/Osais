package testing;

import contabilidad.logica.Mayores;

/**
 *
 * @author bgari
 */
public class TestMayores {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String cuenta = "110002002001";
        String expected = "110000000000110002000000110002002000";
        String result = Mayores.getMayores(cuenta);
        if (result.equals(expected)) {
            System.out.println("Clase mayores funciona adecuadamente.");
        } else {
            System.err.println("La clase mayores no funciona como se esperaba.");
        }
    }
    
}
