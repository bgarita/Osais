package testing;

import java.util.Date;
import logica.utilitarios.Ut;

/**
 *
 * @author Administrador
 */
public class TestgoMonth {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Date d = new Date();
        short meses = 24;
        d = Ut.goMonth(d, meses);
        System.out.println(Ut.dtoc(d));
    } // end main

} // end class
