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
        /*
        Esta cuenta de movimientos debe reportar los montos a estas 3
        cuentas de mayor.
        110000000000
        110002000000
        110002002000
        La clase Mayores genera esas tres cuentas y las concatena en un solo string.
        */
        String expected = "110000000000110002000000110002002000";
        String result = Mayores.getMayores(cuenta);
        if (result.equals(expected)) {
            System.out.println("Clase mayores funciona adecuadamente.");
        } else {
            System.err.println("La clase mayores no funciona como se esperaba.");
        }
        
        cuenta = "110000000000";
        result = Mayores.getMayores(cuenta);
    }
    
}
