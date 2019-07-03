/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import java.util.Calendar;
import java.util.GregorianCalendar;
import logica.utilitarios.Ut;

/**
 *
 * @author Administrador
 */
public class MesLetras {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Calendar cal = GregorianCalendar.getInstance();
        System.out.println(Ut.mesLetras(cal.get(Calendar.MONTH)));
        System.out.println(Ut.diaLetras(cal.get(Calendar.DAY_OF_WEEK)));
        //System.out.println(args[0]);
    }

}
