
package testing;

import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
public class Testrpad {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String n = "12";
        System.out.println(Ut.rpad(n, "0", 5));
        System.out.println(Ut.lpad(n, "0", 5));
        // Cambiar una secuencia de caracteres en un string
        System.out.println(n.replace("2", "3"));

        // Center Pad
        System.out.println(Ut.cpad("123", "=", 22));
    }

}
