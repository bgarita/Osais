package testing;

import logica.utilitarios.Ut;

/**
 *
 * @author bgari
 */
public class TestAT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String cuenta = "110002002004";
        String texto = "2";
        
        int pos = Ut.AT(texto, cuenta);
        System.out.println(pos);
        System.out.println(cuenta.indexOf(texto));
    }
    
}
