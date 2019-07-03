/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
public class Fechas {
    public static String getHoraActual() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ssZ");
        //new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        //new SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ssZhh:mm"); // [Z|(+|-)hh:mm]
        //new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String fecha = formateador.format(ahora);
        int pos = fecha.lastIndexOf("-") + 3;
        fecha = fecha.substring(0,pos) + ":" + fecha.substring(pos);
        return fecha;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(getHoraActual());
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.YEAR, 2012);
        
        System.out.println(Ut.lastDay(cal.getTimeInMillis()));
        
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        
        System.out.println(cal.getTime());
    }
    


}
