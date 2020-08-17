package testing;

import java.util.Date;
import logica.utilitarios.Ut;

/**
 *
 * @author Administrador
 */
public class TestLastDate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Date d = new Date();
        System.out.println(Ut.dtoc(
                Ut.lastDate(d)));
    }

}
