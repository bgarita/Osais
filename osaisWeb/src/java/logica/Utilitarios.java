
package logica;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Bosco
 */
public class Utilitarios {

    /**
     * Este método formatea un string y lo devuelve con separador de miles y
     * decimales o con el formato que se le pase.
     * @param numero Valor a formatear
     * @param formato Máscara que se usará para devolver el dato.  Puede venir
     * nulo o vacío en cuyo caso se usará el valor predeterminado "#,##0.00".
     * @return número formateado y sin espacios.
     */
    public static String Fdecimal(String numero, String formato){
        if (numero == null){
            return "";
        } // end if

        if (numero.trim().equals("")){
            return "";
        } // end if

        // Establezco el formato predeterminado
        if (formato == null || formato.trim().equals(""))
            formato = "#,##0.00";
        // end if

        String devolver = null;
        Double Dnumero = Double.parseDouble(quitarFormato(numero.trim()));
        devolver = new java.text.DecimalFormat(formato).format(Dnumero);
        return devolver.trim();
    } // end Fdecimal


    /**
     * Método para quitar el formato de un string
     * @param valortexto
     * @return String
     */
    public static String quitarFormato(String valortexto) {
        Number valor = null;
        NumberFormat nf = NumberFormat.getNumberInstance();
        try {
            valor = nf.parse(valortexto.trim());
        } catch (ParseException ex) {
            return "";
        }
        return valor.toString();
    } // quitarFormato

    /**
     * Autor: Bosco Garita 12/09/2009
     * Este método convierte de fecha a caracter.
     * @param Dfecha objeto de tipo Date
     * @return String con el formato "dd/mm/aaaa"
     */
    public static String dtoc(Date Dfecha){
        Calendar cal = GregorianCalendar.getInstance();

        if (Dfecha == null)
            return "  /  /    ";
        // end if

        cal.setTime(Dfecha);

        String dia, mes, año;
        dia    = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        mes    = String.valueOf(cal.get(Calendar.MONTH) + 1);
        año    = String.valueOf(cal.get(Calendar.YEAR));
        dia    = dia.length() < 2 ? "0" + dia: dia;
        mes    = mes.length() < 2 ? "0" + mes: mes;

        return dia + "/" + mes + "/" + año;
    } // end dtoc

    /**
     * Autor: Bosco Garita 19/03/2011
     * Si la fecha recibida es null entonces el valor de retorno será "null"
     * Si la hora recibida es null o alguno de los caracteres es inadecuado
     * entonces el valor de retorno será "'aaaa-mm-dd 00:00:00'"
     * Si alguno de los valores de hora minuto o segundo se encuentran fuera
     * del rango respectivo entonces el valor se establecerá a 00 según
     * corresponda.
     * @param Dfecha Fecha a formatear
     * @param horaMilitar Hora a concatenar
     * @return String Fecha y hora con formato SQL "'aaaa-mm-dd hh:mm:ss'"
     */
    public static String fechaHoraSQL(Date Dfecha, String horaMilitar){
        if (Dfecha == null)
            return "null";
        // end if

        // En vez de esto, incluir una función de validación
        // para que verifique cada posición y convierta de hora
        // doce a hora veinticuatro.
        if (horaMilitar == null) horaMilitar = "00:00:00";
        horaMilitar = horaValida(horaMilitar);
        String f = fechaSQL(Dfecha);
        f = f.substring(0,f.length()-1) + " " + horaMilitar + "'";

        return f;
    } // end fechaHoraSQL

    /**
     * Autor: Bosco Garita 19/03/2011
     * Valida que la hora que reciba sea válida.
     * En caso de recibir un caracter inesperado en la posición
     * respectiva (hh:mm:ss) inmediatemante retornará 00:00:00
     * Si alguno de los tres valores númericos no se encuentra
     * dentro del rango adecuado, éste se establecerá a 00 para
     * retornar un valor válido.
     * @param hora String a validar como hora militar
     * @return String (hh:mm:ss) hora militar válida.
     */
    public static String horaValida(String hora){
        String horaM = "00:00:00";
        boolean hayError = false;

        if (hora == null) return horaM;

        hora = hora.trim();

        // Verifico que el patrón de horas sea el correcto
        char[] horaCharArray = hora.toCharArray();
        for (int i = 0; i < horaCharArray.length; i++){
            // Primero reviso el separador...
            if (i == 2 || i == 5){
                if(horaCharArray[i] != ':') hayError = true;
            }else{ // ...luego el número
                if(horaCharArray[i] < '0' || horaCharArray[i] > '9') hayError = true;
            } // end if
        } // end for

        if (hayError) return horaM;

        // Verifico los rangos de hora minuto y segundo
        // Si hay error dejo el rango mínimo
        horaM = "";
        int hh, mm, ss;
        hh = Integer.parseInt(hora.substring(0, 2));
        mm = Integer.parseInt(hora.substring(3, 5));
        ss = Integer.parseInt(hora.substring(6, 8));

        // No se evalúan los negativos porque cualquier valor
        // que no sean número entre 0 y 9 en el for anterior
        // produce que todo el resultado sea 00:00:00
        if (hh > 23) hh = 0;
        if (mm > 59) mm = 0;
        if (ss > 59) ss = 0;

        if (hh < 10) horaM = "0";
        horaM += hh + ":";

        if (mm < 10) horaM += "0";
        horaM += mm + ":";

        if (ss < 10) horaM += "0";
        horaM += ss;

        return horaM;
    } // end horaValida


    /**
     * Este método recibe una cal de tipo Date
     * y lo convierte a un String con la cal formateada para SQL; es
     * decir que incluye la comilla inicial y la comilla final.
     * Ejemplo '2009-31-01' (aaaa-mm-dd)
     * @since 1.0 - 10/07/2010 by Bosco Garita
     * @param Dfecha Fecha de tipo Date
     * @return Fecha sql 'aaaa-mm-dd'
     */
    public static String fechaSQL(Date Dfecha){
        Calendar cal = GregorianCalendar.getInstance();

        if (Dfecha == null)
            return "null";
        // end if

        cal.setTime(Dfecha);

        String dia, mes, año;
        dia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        mes = String.valueOf(cal.get(Calendar.MONTH) + 1);
        año = String.valueOf(cal.get(Calendar.YEAR));
        dia = dia.length() < 2 ? "0" + dia: dia;
        mes = mes.length() < 2 ? "0" + mes: mes;

        return "'" + año + "-" + mes + "-" + dia + "'";
    } // end fechaSQL
} // end class
