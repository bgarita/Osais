/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import logica.utilitarios.Ut;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Bosco Garita
 */
public class TestDates {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Calendar cal = GregorianCalendar.getInstance();
        //cal.add(Calendar.MONTH, -1);
        System.out.println(Ut.lastDay(cal));
    }

}
