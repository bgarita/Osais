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
 * @author Bosco
 */
public class testtimeDiff {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Calendar cal = GregorianCalendar.getInstance();
        Calendar cal2 = GregorianCalendar.getInstance();
        long hora = 1000*60*60;
        int minuto = 1000*60;
        short segundo = 1000;
        // Aumentar 3 horas, 62 minutos, 45 segundos y 950 milisegundos
        // El resultado de esta operación debe ser de:
        // 4 horas, 2 minutos, 45 segundos y 950 milisegundos.
        cal2.setTimeInMillis(
                cal.getTimeInMillis() + (hora*3) // 3 horas
                + (minuto*62)                    // 62 minutos
                + (segundo*45)                   // 45 segundos
                + 950);                          // 950 milisegundos
        int[][] dif = 
                Ut.timeDiff(cal2.getTimeInMillis(), cal.getTimeInMillis());
        System.out.println("La diferencia entre " + cal.getTime() + " y " + cal2.getTime() + " es:");
        System.out.println("Horas: " + dif[0][0]);
        System.out.println("Minutos: " + dif[0][1]);
        System.out.println("Segundos: " + dif[0][2]);
        System.out.println("Milisegundos: " + dif[0][3]);
        
        cal2 = GregorianCalendar.getInstance();
        cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MONTH, -5);
        System.out.print("La diferencia en meses entre " 
                + cal.getTime() + " y " + cal2.getTime() + " es: ");
        System.out.println(Ut.getMonths(cal.getTime(), cal2.getTime()));
        System.out.println("Método sobrecargado: " + Ut.getMonths(cal, cal2));
    }

}
