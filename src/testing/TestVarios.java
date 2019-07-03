/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.util.Calendar;
import java.util.GregorianCalendar;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class TestVarios {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.YEAR, 1966);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 31);
        System.out.println(Ut.calcularEdad(c.getTime()));
    }
    
}
