package logica.utilitarios;

import Exceptions.EmptyDataSourceException;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bosco Garita
 */
public class Ut {

    /**
     * Obtener el número de puerto por el que está escuchando el motor de base de datos.
     *
     * @param url String texto que incluye parte de la conexión a base de datos.
     * @return String número de puerto
     */
    public static String getConnectionPort(String url) {
        int pos = getPosicion(url, ":");
        String temp = url.substring(pos + 1);
        pos = getPosicion(temp, "/");
        return temp.substring(0, pos);
    } // end getConnectionPort

    public static final int DIA = 1;
    public static final int MES = 2;
    public static final int AÑO = 3;

    // Constantes para el método getProperty
    // Variables de entorno.
    public static final int USER_NAME = 1;
    public static final int USER_DIR = 2;
    public static final int USER_HOME = 3;
    public static final int TMPDIR = 4;
    public static final int OS_NAME = 5;
    public static final int OS_VERSION = 6;
    public static final int FILE_SEPARATOR = 7;
    public static final int PATH_SEPARATOR = 8;
    public static final int LINE_SEPARATOR = 9;
    public static final int WINDIR = 10;      // Solo para windows
    public static final int SYSTEM32 = 11;      // Solo para windows
    public static final int COMPUTERNAME = 12;
    public static final int PROCESSOR_IDENTIFIER = 13;
    public static final int JAVA_VERSION = 14;

    // Constantes para navegar en un Result Set
    public static final int BEFORE_FIRST = 1;
    public static final int FIRST = 2;
    public static final int NEXT = 3;
    public static final int PREVIOUS = 4;
    public static final int LAST = 5;
    public static final int AFTER_LAST = 6;
    public static final int ABSOLUTE = 7;

    /**
     * @author: Bosco Garita Azofeifa Este método recibe un objeto de tipo JTextField que
     * contiene un número formateado y devuelve otro objeto de tipo Number con el valor
     * del objeto recibido pero sin formato.
     * @param objeto
     * @return Number
     * @throws java.text.ParseException
     */
    public static Number quitarFormato(JTextField objeto) throws ParseException {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return nf.parse(objeto.getText().trim());
    } // end quitarFormato

    /**
     * @author: Bosco Garita Azofeifa Método sobrecargado para utilizar con String en vez
     * del objeto
     * @param valortexto
     * @return String
     * @throws java.text.ParseException
     */
    public static String quitarFormato(String valortexto) throws Exception {
        if (valortexto != null && valortexto.equals("NaN")) {
            return "0";
        }

        Number valor;
        NumberFormat nf = NumberFormat.getNumberInstance();
        try {
            valortexto = valortexto == null ? "0" : valortexto.trim();
            valor = nf.parse(valortexto);
        } catch (ParseException ex) {
            valor = nf.parse("0");
        }
        return valor.toString();
    } // quitarFormato

    /**
     * Este método formatea un string y lo devuelve con separador de miles y decimales o
     * con el formato que se le pase.
     *
     * @param numero Valor a formatear
     * @param formato Máscara que se usará para devolver el dato. Puede venir nulo o vacío
     * en cuyo caso se usará el valor predeterminado "#,##0.00".
     * @return número formateado y sin espacios. (Redondea).
     * @throws java.text.ParseException
     */
    public static String setDecimalFormat(String numero, String formato) throws Exception {
        if (numero == null) {
            return "";
        } // end if

        if (numero.trim().equals("")) {
            return "";
        } // end if

        // Establezco el formato predeterminado
        if (formato == null || formato.trim().equals("")) {
            formato = "#,##0.00";
        } // end if

        String devolver;
        Double Dnumero = Double.parseDouble(quitarFormato(numero.trim()));
        devolver = new java.text.DecimalFormat(formato).format(Dnumero);
        return devolver.trim();
    } // end setDecimalFormat

    /**
     * Este método formatea un string y lo devuelve con separador de miles.
     *
     * @param numero Valor a formatear
     * @param decimales cantidad posiciones decimales a mostrar. Redondea.
     * @return número formateado y sin espacios.
     * @throws java.text.ParseException
     */
    public static String setDecimalFormat(String numero, int decimales) throws Exception {
        String formato;
        if (numero == null) {
            return "";
        } // end if

        if (numero.trim().equals("")) {
            return "";
        } // end if

        // Establezco el formato
        formato = "%,." + decimales + "f";

        String devolver;
        Double Dnumero = Double.parseDouble(quitarFormato(numero.trim()));
        devolver = String.format(formato, Dnumero);
        return devolver.trim();
    } // end setDecimalFormat

    /**
     * Este método calcula la eda en años meses y días
     *
     * @param fechaNacimiento tipo Date
     * @return una cadena como "nn años, nn meses, nn días."
     */
    public static String calcularEdad(Date fechaNacimiento) {
        Calendar fechaNac = GregorianCalendar.getInstance();
        Calendar fechaAct = GregorianCalendar.getInstance();
        String edad;
        fechaNac.setTime(fechaNacimiento);

        int anos = fechaAct.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
        int meses = fechaAct.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);
        int dias = fechaAct.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH);
        int diasMes = lastDay(fechaAct.getTime());

        int mesNac, mesAct;
        // Los meses en Java empiezan desde cero
        mesNac = fechaNac.get(Calendar.MONTH) + 1;
        mesAct = fechaAct.get(Calendar.MONTH) + 1;

        if (mesNac > mesAct || (mesNac == mesAct
                && fechaNac.get(Calendar.DAY_OF_MONTH)
                > fechaAct.get(Calendar.DAY_OF_MONTH))) {
            anos -= 1;
            meses = 12 - mesNac + mesAct;
        } // end if
        if (fechaNac.get(Calendar.DAY_OF_MONTH)
                > fechaAct.get(Calendar.DAY_OF_MONTH)) {
            meses -= 1;
            dias = diasMes - fechaNac.get(Calendar.DAY_OF_MONTH)
                    + fechaAct.get(Calendar.DAY_OF_MONTH);
        } // end if

        anos = anos < 0 ? 0 : anos;
        meses = meses < 0 ? 0 : meses;
        dias = dias < 0 ? 0 : dias;

        edad = Integer.toString(anos).trim() + " años,"
                + Integer.toString(meses).trim() + " meses,"
                + Integer.toString(dias).trim() + " días.";

        return edad;
    } // calcularEdad

    /**
     * Este método calcula el último día para el mes y año de la fecha recibida en el
     * parámetro dFecha que es de tipo Date.
     *
     * @param dFecha Fecha recibida
     * @return valor numérico (int) que representa el último día.
     */
    public static int lastDay(Date dFecha) {
        Calendar fecha = GregorianCalendar.getInstance();
        fecha.setTime(dFecha);
        return lastDay(fecha);
    } // lastDay

    /**
     * Este método calcula el último día para el mes y año de la fecha recibida en el
     * parámetro fecha que es de tipo long.
     *
     * @author Bosco Garita A. 03/11/2013
     * @param fecha long fecha recibida
     * @return int último día
     */
    public static int lastDay(long fecha) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(fecha);
        return lastDay(cal);
    } // end lastDay

    /**
     * Este método calcula el último día para el mes y año de la fecha recibida en el
     * parámetro cal que es de tipo Calendar. Además deja el parámetro establecido con la
     * fecha del último día del mes.
     *
     * @author Bosco Garita A. 23/05/2014
     * @param cal Calendar fecha recibida
     * @return int último día
     */
    public static int lastDay(Calendar cal) {
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.get(Calendar.DAY_OF_MONTH);
    } // lastDay

    /**
     * @author: Bosco Garita año 2010 Calcular la última fecha para un mes basándose en la
     * fecha recibida
     * @param dFecha - fecha que contiene el mes cuyo último día se calculará
     * @return Date. Fecha que contiene el último día del mes.
     */
    public static Date lastDate(Date dFecha) {
        short meses = 1;

        // Agregar un mes a la fecha recibida
        Date d = Ut.goMonth(dFecha, meses);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(d);

        // A la nueva fecha se le cambia el día por el primer día del mes
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // A la nueva fecha se le resta un día y de esta manera se obtiene
        // el último día del mes para la fecha recibida.
        cal.add(Calendar.DAY_OF_MONTH, -1);
        d.setTime(cal.getTimeInMillis());
        return d;
    } // lastDate

    public static String fechaSQL(String fechaSPF) {
        // Si el String recibido está nulo o viene vacío o solo con los
        // slashes se devuelve un null.
        if (fechaSPF == null || fechaSPF.trim().equals("")
                || fechaSPF.trim().endsWith("/")) {
            return null;
        } // end if
        if (fechaSPF.equalsIgnoreCase("NULL")) {
            return null;
        } // end if
        String dia, mes, año;
        fechaSPF = fechaSPF.trim();
        dia = fechaSPF.substring(0, 2);
        mes = fechaSPF.substring(3, 5);
        año = fechaSPF.substring(6, 10);
        return "'" + año + "-" + mes + "-" + dia + "'";
    } // end fechaSQL

    /**
     * Recibe un string con un formato de fecha en español (dd/mm/aaaa) y lo convierte a
     * formato de fecha SQL (aaaa-mm-dd). Opcionalmente puede agregar el apóstrofo inicial
     * y final a la cadena.
     *
     * @param fechaSPF String --> Fecha con formato dd/mm/aaaa
     * @param apostrofo boolean --> Indica si la cadena con fecha SQL contendrá los
     * apóstrofos inicial y final en cuyo caso sería: 'aaaa-mm-dd'
     * @return String --> fecha con formato SQL.
     */
    public static String fechaSQL(String fechaSPF, boolean apostrofo) {
        // Si el String recibido está nulo o viene vacío o solo con los
        // slashes se devuelve un null.
        if (fechaSPF == null || fechaSPF.trim().equals("")
                || fechaSPF.trim().endsWith("/")) {
            return null;
        } // end if
        if (fechaSPF.equalsIgnoreCase("NULL")) {
            return null;
        } // end if
        String dia, mes, año, fecha;
        fechaSPF = fechaSPF.trim();
        dia = fechaSPF.substring(0, 2);
        mes = fechaSPF.substring(3, 5);
        año = fechaSPF.substring(6, 10);
        fecha = año + "-" + mes + "-" + dia;
        if (apostrofo) {
            fecha = "'" + año + "-" + mes + "-" + dia + "'";
        } // end if
        return fecha;
    } // end fechaSQL

    /**
     * Este método recibe una variable de tipo Date y la convierte a un String con la
     * fecha formateada para SQL; es decir que incluye la comilla inicial y la comilla
     * final. Ejemplo '2009-31-01' (aaaa-mm-dd)
     *
     * @since 1.0 - 10/07/2010 by Bosco Garita
     * @param dFecha Date
     * @return Fecha String sql 'aaaa-mm-dd'
     */
    public static String fechaSQL(Date dFecha) {
        Calendar cal = GregorianCalendar.getInstance();

        if (dFecha == null) {
            return "null";
        } // end if

        cal.setTime(dFecha);

        String dia, mes, año;
        dia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        mes = String.valueOf(cal.get(Calendar.MONTH) + 1);
        año = String.valueOf(cal.get(Calendar.YEAR));
        dia = dia.length() < 2 ? "0" + dia : dia;
        mes = mes.length() < 2 ? "0" + mes : mes;

        return "'" + año + "-" + mes + "-" + dia + "'";
    } // end fechaSQL

    /**
     * Autor: Bosco Garita 02/05/2011. Esta función es idéntica a fechaSQL excepto por el
     * resultado que va sin los apóstofos.
     *
     * @param Dfecha Date a convertir
     * @return String con formato aaaa-mm-dd (sin apóstrofes)
     */
    public static String fechaSQL2(Date Dfecha) {
        String fecha = fechaSQL(Dfecha);
        fecha = fecha.substring(1, fecha.lastIndexOf("'"));
        return fecha;
    } // end fechaSQL2

    /**
     * Autor: Bosco Garita 19/03/2011 Si la fecha recibida es null entonces el valor de
     * retorno será "null" Si la hora recibida es null o alguno de los caracteres es
     * inadecuado entonces el valor de retorno será "'aaaa-mm-dd 00:00:00'" Si alguno de
     * los valores de hora minuto o segundo se encuentran fuera del rango respectivo
     * entonces el valor se establecerá a 00 según corresponda.
     *
     * @param Dfecha Fecha a formatear
     * @param horaMilitar Hora a concatenar
     * @return String Fecha y hora con formato SQL "'aaaa-mm-dd hh:mm:ss'"
     */
    public static String fechaHoraSQL(Date Dfecha, String horaMilitar) {
        if (Dfecha == null) {
            return "null";
        } // end if

        // En vez de esto, incluir una función de validación
        // para que verifique cada posición y convierta de hora
        // doce a hora veinticuatro.
        if (horaMilitar == null) {
            horaMilitar = "00:00:00";
        } // end if
        horaMilitar = horaValida(horaMilitar);
        String f = fechaSQL(Dfecha);
        f = f.substring(0, f.length() - 1) + " " + horaMilitar + "'";

        return f;
    } // end fechaHoraSQL

    /**
     * Autor: Bosco Garita 19/03/2011 Valida que la hora que reciba sea válida. En caso de
     * recibir un caracter inesperado en la posición respectiva (hh:mm:ss) inmediatemante
     * retornará 00:00:00 Si alguno de los tres valores númericos no se encuentra dentro
     * del rango adecuado, éste se establecerá a 00 para retornar un valor válido.
     *
     * @param hora String a validar como hora militar
     * @return String (hh:mm:ss) hora militar válida.
     */
    public static String horaValida(String hora) {
        String horaM = "00:00:00";
        boolean hayError = false;

        if (hora == null) {
            return horaM;
        } // end if

        hora = hora.trim();

        // Verifico que el patrón de horas sea el correcto
        char[] horaCharArray = hora.toCharArray();
        for (int i = 0; i < horaCharArray.length; i++) {
            // Primero reviso el separador...
            if (i == 2 || i == 5) {
                if (horaCharArray[i] != ':') {
                    hayError = true;
                } // end if
            } else { // ...luego el número
                if (horaCharArray[i] < '0' || horaCharArray[i] > '9') {
                    hayError = true;
                } // end if
            } // end if
        } // end for

        if (hayError) {
            return horaM;
        } // end if

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
        if (hh > 23) {
            hh = 0;
        }
        if (mm > 59) {
            mm = 0;
        }
        if (ss > 59) {
            ss = 0;
        }

        if (hh < 10) {
            horaM = "0";
        }
        horaM += hh + ":";

        if (mm < 10) {
            horaM += "0";
        }
        horaM += mm + ":";

        if (ss < 10) {
            horaM += "0";
        }
        horaM += ss;

        return horaM;
    } // end horaValida

    /**
     * Autor: Bosco Garita METODO PARA MSSQLSERVER. Este método recibe una cal de tipo
     * Date y lo convierte a un String que incluye la función CONVERT() de sql. Este
     * método es el más recomendado, sobre todo cuando no se tiene seguridad del formato
     * de cal que utiliza SQL para convertir de caracter a cal. Ejemplo
     * convert(datetime,'25/01/2009',103)
     *
     * @param Dfecha
     * @return convert(datetime,'dd/mm/aaaa',103)
     */
    public static String fechaSQLSPF(Date Dfecha) {
        Calendar cal = GregorianCalendar.getInstance();

        if (Dfecha == null) {
            return "null";
        } // end if

        cal.setTime(Dfecha);

        String dia, mes, año, Sfecha;
        dia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        mes = String.valueOf(cal.get(Calendar.MONTH) + 1);
        año = String.valueOf(cal.get(Calendar.YEAR));
        dia = dia.length() < 2 ? "0" + dia : dia;
        mes = mes.length() < 2 ? "0" + mes : mes;

        Sfecha = "'" + dia + "/" + mes + "/" + año + "'";

        return Sfecha;
    } // end fechaSQLSPF

    // Este método no es estático porque usa variables miembro.
    public int getDays(String fecha1, String fecha2, int iniciaCon) {
        int dia1 = 0, mes1 = 0, año1 = 0;
        int dia2 = 0, mes2 = 0, año2 = 0;

        if (iniciaCon < DIA || iniciaCon > AÑO) {
            iniciaCon = DIA;
        } // end if

        // Determino con qué inicia la Sfecha.  Los valores aceptados son:
        // DIA = 1 (dd/mm/aaaa)
        // MES = 2 (mm/dd/aaaa)
        // AÑO = 3 (aaaa/mm/dd)
        switch (iniciaCon) {
            case DIA:
                dia1 = Integer.parseInt(fecha1.substring(0, 2));
                mes1 = Integer.parseInt(fecha1.substring(3, 5));
                año1 = Integer.parseInt(fecha1.substring(6, 10));
                dia2 = Integer.parseInt(fecha2.substring(0, 2));
                mes2 = Integer.parseInt(fecha2.substring(3, 5));
                año2 = Integer.parseInt(fecha2.substring(6, 10));
                break;
            case MES:
                mes1 = Integer.parseInt(fecha1.substring(0, 2));
                dia1 = Integer.parseInt(fecha1.substring(3, 5));
                año1 = Integer.parseInt(fecha1.substring(6, 10));
                mes2 = Integer.parseInt(fecha2.substring(0, 2));
                dia2 = Integer.parseInt(fecha2.substring(3, 5));
                año2 = Integer.parseInt(fecha2.substring(6, 10));
                break;
            case AÑO:
                año1 = Integer.parseInt(fecha1.substring(0, 4));
                mes1 = Integer.parseInt(fecha1.substring(5, 7));
                dia1 = Integer.parseInt(fecha1.substring(8, 10));
                año2 = Integer.parseInt(fecha2.substring(0, 4));
                mes2 = Integer.parseInt(fecha2.substring(5, 7));
                dia2 = Integer.parseInt(fecha2.substring(8, 10));
                break;
        } // end swtch

        GregorianCalendar xfecha1 = new GregorianCalendar(año1, mes1, dia1);
        GregorianCalendar xfecha2 = new GregorianCalendar(año2, mes2, dia2);

        Date fec1 = xfecha1.getTime();
        Date fec2 = xfecha2.getTime();

        long time = fec2.getTime() - fec1.getTime();
        int dias = (int) (time / (3600 * 24 * 1000));

        return dias;
    } // end getDays

    public int getDays(Long fecha1, Long fecha2, int iniciaCon) {
        if (iniciaCon < DIA || iniciaCon > AÑO) {
            iniciaCon = DIA;
        } // end if
        Calendar xfecha1 = new GregorianCalendar();
        Calendar xfecha2 = new GregorianCalendar();
        xfecha1.setTimeInMillis(fecha1);
        xfecha2.setTimeInMillis(fecha2);
        String año1, mes1, dia1, año2, mes2, dia2, yfecha1 = null, yfecha2 = null;
        año1 = String.valueOf(xfecha1.get(Calendar.YEAR));
        año2 = String.valueOf(xfecha2.get(Calendar.YEAR));
        mes1 = String.valueOf(xfecha1.get(Calendar.MONTH));
        mes2 = String.valueOf(xfecha2.get(Calendar.MONTH));
        dia1 = String.valueOf(xfecha1.get(Calendar.DAY_OF_MONTH));
        dia2 = String.valueOf(xfecha2.get(Calendar.DAY_OF_MONTH));
        if (dia1.length() < 2) {
            dia1 = "0" + dia1;
        } // end if
        if (mes1.length() < 2) {
            mes1 = "0" + mes1;
        } // end if
        if (dia2.length() < 2) {
            dia2 = "0" + dia2;
        } // end if
        if (mes2.length() < 2) {
            mes2 = "0" + mes2;
        } // end if
        switch (iniciaCon) {
            case DIA:
                yfecha1 = dia1 + "/" + mes1 + "/" + año1;
                yfecha2 = dia2 + "/" + mes2 + "/" + año2;
                break;
            case MES:
                yfecha1 = mes1 + "/" + dia1 + "/" + año1;
                yfecha2 = mes2 + "/" + dia2 + "/" + año2;
                break;
            case AÑO:
                yfecha1 = año1 + "/" + mes1 + "/" + dia1;
                yfecha2 = año2 + "/" + mes2 + "/" + dia2;
                break;
        } // end switch
        return getDays(yfecha1, yfecha2, iniciaCon);
    } // getDays

    public static int getDays(Long fecha1, Long fecha2) {
        long time = fecha2 - fecha1;
        int dias = (int) (time / (3600 * 24 * 1000));
        return dias;
    } // end getDays

    /**
     * @author Bosco Garita 15/11/2011 11:09 PM Este método calcula la diferencia entre
     * dos valores de tipo long y convierte la diferencia a horas, minutos, segundos y
     * milisegundos.
     * @param fechaMayor long que representa una fecha (la fecha mayor)
     * @param fechaMenor long que representa una fecha (la fecha menor)
     * @return int[1][4] hora, minuto, segundo, milisegundo
     */
    public static int[][] timeDiff(long fechaMayor, long fechaMenor) {
        int[][] difference = new int[1][4];
        long miliSeg = fechaMayor - fechaMenor;
        // Calcular las horas
        difference[0][0] = (int) miliSeg / 1000 / 60 / 60;
        miliSeg -= difference[0][0] * 1000 * 60 * 60;
        // Calcular los minutos
        difference[0][1] = (int) miliSeg / 1000 / 60;
        miliSeg -= difference[0][1] * 1000 * 60;
        // Calcular los segundos
        difference[0][2] = (int) miliSeg / 1000;
        miliSeg -= difference[0][2] * 1000;
        // El remanente corresponde a los milisegundos
        difference[0][3] = (int) miliSeg;
        return difference;
    } // end timeDiff

    /**
     * @author Bosco Garita 12/09/2009 Este método devuelve la diferencia en meses entre
     * dos fechas. Ver también el método timeDiff() Recibe dos strings con las fechas a
     * procesar. El formato deberá venir en yyyy/mm/dd.
     * @param fecha1 "yyyy/mm/dd" Sfecha menor
     * @param fecha2 "yyyy/mm/dd" Sfecha mayor
     * @return número de meses
     */
    public static int getMonths(String fecha1, String fecha2) {
        String f1 = fecha1; // "2007/03/03";
        String f2 = fecha2; // "2008/02/02";
        String[] aF1 = f1.split("/");
        String[] aF2 = f2.split("/");
        Integer numMeses
                = Integer.parseInt(aF2[0]) * 12
                + Integer.parseInt(aF2[1]) - (Integer.parseInt(aF1[0]) * 12
                + Integer.parseInt(aF1[1]));
        if (Integer.parseInt(aF2[2]) < Integer.parseInt(aF1[2])) {
            numMeses -= 1;
        } // end fif
        return numMeses;
    } // end getMonths

    /**
     * @author Bosco Garita 08/05/2012 Este método devuelve la diferencia en meses entre
     * dos fechas. Ver también el método timeDiff() Recibe dos Dates con las fechas a
     * procesar.
     * @param fecha1 Date fecha menor
     * @param fecha2 Date fecha mayor
     * @return número de meses
     */
    public static int getMonths(Date fecha1, Date fecha2) {
        int meses;
        Calendar calA = GregorianCalendar.getInstance();
        Calendar calB = GregorianCalendar.getInstance();
        calA.setTime(fecha1);
        calB.setTime(fecha2);

        String sFecha1, sFecha2;
        sFecha1
                = calA.get(Calendar.YEAR) + "/"
                + (calA.get(Calendar.MONTH) + 1) + "/"
                + calA.get(Calendar.DAY_OF_MONTH);
        sFecha2
                = calB.get(Calendar.YEAR) + "/"
                + (calB.get(Calendar.MONTH) + 1) + "/"
                + calB.get(Calendar.DAY_OF_MONTH);
        meses = getMonths(sFecha1, sFecha2);
        return meses;
    } // end getMonths

    /**
     * @author Bosco Garita 08/05/2012 Este método devuelve la diferencia en meses entre
     * dos fechas. Ver también el método timeDiff() Recibe dos Calendar con las fechas a
     * procesar.
     * @param fecha1 Calendar fecha menor
     * @param fecha2 Calendar fecha mayor
     * @return número de meses
     */
    public static int getMonths(Calendar fecha1, Calendar fecha2) {
        int meses;
        String sFecha1, sFecha2;
        sFecha1
                = fecha1.get(Calendar.YEAR) + "/"
                + (fecha1.get(Calendar.MONTH) + 1) + "/"
                + fecha1.get(Calendar.DAY_OF_MONTH);
        sFecha2
                = fecha2.get(Calendar.YEAR) + "/"
                + (fecha2.get(Calendar.MONTH) + 1) + "/"
                + fecha2.get(Calendar.DAY_OF_MONTH);
        meses = getMonths(sFecha1, sFecha2);
        return meses;
    } // end getMonths

    /**
     * Autor: Bosco Garita 12/09/2009 Este método convierte de cal a caracter.
     *
     * @param Dfecha objeto de tipo Date
     * @return String con el formato "dd/mm/aaaa"
     */
    public static String dtoc(Date Dfecha) {
        Calendar cal = GregorianCalendar.getInstance();

        if (Dfecha == null) {
            return "  /  /    ";
        } // end if

        cal.setTime(Dfecha);

        String dia, mes, año;
        dia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        mes = String.valueOf(cal.get(Calendar.MONTH) + 1);
        año = String.valueOf(cal.get(Calendar.YEAR));
        dia = dia.length() < 2 ? "0" + dia : dia;
        mes = mes.length() < 2 ? "0" + mes : mes;

        return dia + "/" + mes + "/" + año;
    } // end dtoc

    /**
     * @author Bosco Garita Este método recibe un String con formato de fecha dd/mm/aaaa y
     * lo convierte a un objeto de tipo Date.
     * @param fechaSPF String fecha con formato castellano.
     * @return java.sql.Date
     */
    public static Date ctod(String fechaSPF) {
        if (fechaSPF == null || fechaSPF.trim().equals("")
                || fechaSPF.trim().endsWith("/")) {
            return null;
        } // end if
        if (fechaSPF.equalsIgnoreCase("NULL")) {
            return null;
        } // end if
        String dia, mes, año;
        fechaSPF = fechaSPF.trim();
        dia = fechaSPF.substring(0, Ut.AT(fechaSPF, "/"));
        dia = dia.length() == 1 ? "0" + dia : dia;
        mes = fechaSPF.substring((Ut.AT(fechaSPF, "/") + 1), Ut.AT(fechaSPF, "/", 2));
        mes = mes.length() == 1 ? "0" + mes : mes;
        año = fechaSPF.substring((Ut.AT(fechaSPF, "/", 2) + 1));
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dia));
        cal.set(Calendar.MONTH, Integer.parseInt(mes) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(año));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date d = new Date();
        d.setTime(cal.getTimeInMillis());
        return d;
    } // end ctod

    /**
     * @author: Bosco Garita A. 07/11/2010. Realiza la misma tarea que GOMONTH() en Visual
     * Fox. Es dicir devuelve una cal aumentada o disminuida n meses dependiendo del valor
     * que reciba en meses. Si el valor recibido en meses es negativo disminuye y si es
     * positivo aumenta.
     * @param dFecha Fecha sobre la cual se aumentará o disminuirá
     * @param meses Número de meses a aumentar o disminuir
     * @return Date Fecha aumentada o disminuida.
     */
    public static Date goMonth(Date dFecha, short meses) {
        Date d = new Date();
        Calendar cal = GregorianCalendar.getInstance();

        if (dFecha == null) {
            return dFecha;
        } // end if

        cal.setTime(dFecha);
        cal.add(Calendar.MONTH, meses);
        d.setTime(cal.getTimeInMillis());
        return d;
    } // end goMonth

    /**
     * Este método simula la función Seek() de fox. Buscar un valor de tipo String en un
     * ResultSet.
     *
     * @param rs ResultSet en donde se hará la búsqueda. El Statement que lo generó deberá
     * tener la propieada de ResultSet.TYPE_SCROLL_SENSITIVE.
     * @param valor String que se buscará.
     * @param columna Nombre del campo o columna en donde se ejecutará la búsqueda.
     * @return true (valor encontrado), false (valor no encontrado)
     * @throws SQLException
     */
    public static boolean seek(ResultSet rs, String valor, String columna) throws SQLException {
        // Si alguno de los parámetros recibidos está nulo automáticamente
        // la búsqueda es también nula y por lo tanto el resultado es false.
        if (rs == null || valor == null || columna == null) {
            return false;
        } // end if

        valor = valor.trim();
        boolean existe = false;

        rs.beforeFirst();
        while (rs.next()) {
            if (rs.getString(columna).trim().equals(valor)) {
                existe = true;
                break;
            } // end if
        } // end while

        // Si el valor no es encontrado ubico el puntero en EOF
        if (!existe) {
            rs.afterLast();
        }

        return existe;
    } // end seek

    /**
     * Separa un String y devuelve código o descripción según el valor de index
     *
     * @param text String texto a separar
     * @param separator String caracter separador
     * @param index int 0=Codigo, 1=Descripcion
     * @return String código o descripción según index
     */
    public static String getCode(String text, String separator, int index) {
        String[] array = text.split(separator);
        return array[index];
    } // end getCode

    /**
     * Este método simula la función Seek() de fox. Buscar un valor de tipo int en un
     * ResultSet. Al igual que fox el puntero queda ubicado en el registro correspondiente
     * si lo encuentra o en eof() si no lo encuentra.
     *
     * @param rs ResultSet en donde se hará la búsqueda. El Statement que lo generó deberá
     * tener la propieada de ResultSet.TYPE_SCROLL_SENSITIVE.
     * @param buscar int valor a buscar
     * @param buscarEn Nombre del campo en donde se ejecutará la búsqueda.
     * @return true (valor encontrado), false (valor no encontrado)
     * @throws SQLException
     */
    public static boolean seek(ResultSet rs, int buscar, String buscarEn) throws SQLException {
        // Si alguno de los parámetros recibidos (primero o último) está nulo
        // automáticamente la búsqueda es también nula y por lo tanto el
        // resultado es false.
        if (rs == null || buscarEn == null) {
            return false;
        }
        // end if
        boolean existe = false;

        rs.beforeFirst();
        while (rs.next()) {
            if (rs.getInt(buscarEn) == buscar) {
                existe = true;
                break;
            } // end if
        } // end while
        // Si el valor no es encontrado ubico el puntero en EOF
        if (!existe) {
            rs.afterLast();
        }

        return existe;
    } // end seek

    /**
     * Autor: Bosco Garita. 30/01/2011. Busca un valor de tipo String en una JTable. Si el
     * valor es encontrado devolverá true y además seleccionará el dato encontrado.
     *
     * @param table JTable en donde se realiza la búsqueda
     * @param valor String Valor a buscar
     * @param column int columna en donde se buscará
     * @return true, false
     */
    public static boolean seek(JTable table, String valor, int column) {
        if (valor == null) {
            return false;
        }
        // end if

        valor = valor.trim();
        boolean existe = false, toggle = false, extend = false;
        for (int row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, column) == null) {
                continue;
            }
            // end if
            if (table.getValueAt(row, column).toString().trim().equals(valor)) {
                existe = true;
                table.changeSelection(row, column, toggle, extend);
                break;

            } // end if
        } // end for
        return existe;
    } // end seek

    /**
     * Autor: Bosco Garita. 30/01/2011. Busca un valor de tipo int en una JTable. Si el
     * valor es encontrado devolverá true y además seleccionará el dato encontrado.
     *
     * @param table JTable en donde se realiza la búsqueda
     * @param valor int Valor a buscar
     * @param column int columna en donde se buscará
     * @return true, false
     */
    public static boolean seek(JTable table, int valor, int column) {
        boolean existe = false, toggle = false, extend = false;
        String valorx = String.valueOf(valor);
        for (int row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, column) == null) {
                continue;
            }
            // end if
            // Si el valor es encontrado selecciono la fila en donde aparece
            if (table.getValueAt(row, column).toString().trim().equals(valorx)) {
                existe = true;
                table.changeSelection(row, column, toggle, extend);
                break;
            } // end if
        } // end for
        return existe;
    } // end seek

    /**
     * Autor: Bosco Garita 13/09/2009 Devuelve el número de fila en donde se encontró el
     * valor, <br>
     * -1 si el valor no es encontrado.
     *
     * @param table JTable en donde se hará la búsqueda
     * @param valor Object valor a buscar
     * @param column int columna en donde se realizará la búsqueda
     * @return int fila en donde se encontró el valor o -1 si no se encontró
     */
    public static int seek(JTable table, Object valor, int column) {
        if (valor == null) {
            return -1;
        }
        // end if

        String sValor = valor.toString().trim();
        boolean toggle = false, extend = false;
        String sCelda;
        int row;
        for (row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, column) == null) {
                continue;
            }
            // end if
            sCelda = table.getValueAt(row, column).toString().trim();
            if (sCelda.equals(sValor)) {
                table.changeSelection(row, column, toggle, extend);
                break;
            } // end if
        } // end for
        if (row >= table.getRowCount()) {
            row = -1;
        }
        // end if
        return row;
    } // end seek

    /**
     * Autor: Bosco Garita 20/09/2020 Devuelve el número de fila en donde se encontraron
     * los valores, <br>
     * -1 si el valor no es encontrado.
     *
     * @param table JTable en donde se hará la búsqueda
     * @param valores Object[] valore a buscar
     * @param columns int[] columnas en donde se realizará la búsqueda
     * @return int fila en donde se encontraron los valores o -1 si no se encontró
     */
    public static int seek(JTable table, Object[] valores, Integer[] columns) {
        if (valores == null) {
            return -1;
        } // end if

        // Trato de localizar la primera columna que viene en el arreglo.
        // Si no existe no continúo con la búsqueda
        int fila = seek(table, valores[0], columns[0]);

        if (fila < 0) {
            return -1;
        } // end if

        // Concateno todos los valores del arreglo para compararlos
        String aValores = "";
        String tValores;
        for (Object o : valores) {
            aValores += o;
        } // end for

        // Ahora concateno los valores de la tabla para hacer la comparación
        int row;
        boolean found = false;
        for (row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, 0) == null) {
                continue;
            }

            // Concateno los valores de las columnas de acuerdo con los índices que vienen en el arreglo
            tValores = "";
            for (int col = 0; col < table.getColumnCount(); col++) {
                for (int i = 0; i < columns.length; i++) {
                    if (columns[i] == col) {
                        tValores += table.getValueAt(row, col);
                    } // end if
                } // end for
            } // end for

            if (aValores.equals(tValores)) {
                boolean toggle = false, extend = false;
                table.changeSelection(row, 0, toggle, extend);
                found = true;
                break;
            } // end if
        } // end for

        if (found) {
            return row;
        } else {
            return -1;
        }

    } // end seek

    /**
     * Busca dos valores en dos columnas distintas pero en la misma fila <br>
     * si la búsqueda es exitosa devuelve el número de fila, caso contrario <br>
     *
     * @author: Bosco Garita 13/09/2009 devuelve -1.
     * @param table JTable en donde se hará la búsqueda
     * @param valor1 Object primer valor a buscar
     * @param column1 int columna en donde se realizará la búsqueda para el primer valor
     * @param valor2 Object segundo valor a buscar
     * @param column2 int columna en donde se realizará la búsqueda para el segundo valor
     * @return int fila en donde se encontró el valor o -1 si no se encontró
     */
    public static int seek(JTable table, Object valor1, int column1,
            Object valor2, int column2) {
        if (valor1 == null || valor2 == null) {
            return -1;
        } // end if

        int row;
        for (row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, column1) == null) {
                continue;
            }// end if
            if (table.getValueAt(row, column1).equals(valor1)
                    & table.getValueAt(row, column2).equals(valor2)) {
                break;
            } // end if
        } // end for
        if (row >= table.getRowCount()) {
            row = -1;
        } // end if
        return row;
    } // end seek

    /**
     * Cuenta la cantidad de datos no nulos en una tabla para una columna específica.
     *
     * @author: Bosco Garita 13/09/2009
     * @param table JTable a revisar
     * @param column número de columna a revisar
     * @return notNullRecords (Cantidad de registros no nulos)
     */
    public static int countNotNull(JTable table, int column) {
        int notNullRecords = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, column) != null) {
                notNullRecords++;
            }
        } // end for
        return notNullRecords;
    } // end countNotNull

    /**
     * Cuenta la cantidad de datos no nulos y con el valor de true en una tabla para una
     * columna específica.
     *
     * @author Bosco Garita Azofeifa
     * @param table JTable a revisar
     * @param column número de columna a revisar
     * @return countForTrue (Cantidad de registros no nulos)
     */
    public static int countForTrue(JTable table, int column) {
        int trueRecords = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, column) != null
                    && Boolean.parseBoolean(table.getValueAt(i, column).toString())) {
                trueRecords++;
            } // end if
        } // end for
        return trueRecords;
    } // end countForTrue

    /**
     * Este método busca dentro de una JTable la primera fila nula en la columna
     * especificada y retorna el número de fila. En caso de no existir ninguna fila nula
     * devolverá un -1.
     *
     * @param table JTable en donde se realizará la búsqueda
     * @param column int columna que será consultada
     * @return row (Fila encontrada con el valor nulo) -1 si no hay nulos.
     */
    public static int seekNull(JTable table, int column) {
        if (column >= table.getColumnCount()) {
            column = 0;
        } // end if
        int row;
        for (row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, column) == null) {
                break;
            } // end if
        } // end for

        // Si no hay filas nulas en la columna solicitada entonces devuelvo -1
        if (row >= table.getRowCount()) {
            row = -1;
        } // end if
        return row;
    } // end seekNull

    /**
     * @author: Bosco Garita 24/11/2013 Devuelve el mínimo de una columna con valores
     * numéricos. Si no se encontró devolverá 0
     * @param table JTable tabla que será procesada
     * @param column int número de columna que será procesada
     * @return int valor mínimo encontrado
     */
    public static int getMin(JTable table, int column) {
        if (column < 0 || column > table.getColumnModel().getColumnCount()) {
            return 0;
        } // end if

        int minimo = 0;
        int valor;
        String celda;
        int row;
        for (row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, column) == null) {
                continue;
            } // end if

            celda = table.getValueAt(row, column).toString().trim();

            try {
                valor = Integer.parseInt(celda);
            } catch (NumberFormatException ex) {
                continue;
            } // end try-catch

            if (valor < minimo) {
                minimo = valor;
            } // end if
        } // end for

        return minimo;
    } // end getMin

    /**
     * Busca la siguiente fila con valor null en la columna cero, si no la encuentra,
     * agrega una nueva fila. IMPORTANTE: Si la columna cero debe tener valores nulos, no
     * use este método.
     *
     * @param tblX JTable tabla sobre la cual se realizará la acción.
     * @return int número de fila agregada o con null en la columna cero.
     */
    public static int appendBlank(JTable tblX) {
        int row = Ut.seekNull(tblX, 0);
        if (row < 0) {
            Ut.resizeTable(tblX, 1, "Filas");
            row = Ut.seekNull(tblX, 0);
        } // end if
        return row;
    } // end appendBlank

    /**
     * Este método busca dentro de un arreglo bidimensional y retorna el índice en donde
     * se encuentra el valor buscado. Este valor a buscar depende de la columna que se
     * especifique mediante el parámetro column. En caso de no existir el valor
     * especificado devolverá un -1.
     *
     * @param array String[][] arreglo en donde se realizará la búsqueda
     * @param valor String valor a buscar
     * @param column int Columna en donde se realizará la búsqueda
     * @return int (Fila en donde se encontró el valor) -1 si no existe.
     */
    public static int seek(String[][] array, String valor, int column) {
        // Si el parámetro recibido en column es incorrecto se establece
        // la primera columna como default.
        if (column >= array.length) {
            column = 0;
        } // end if

        // No se admiten valores nulos y por lo tanto no se hace la validación
        // para que de error y corregir el programa que invoque este método.
        valor = valor.trim();
        int row = -1; // Es necesario inicializarlo así.
        for (row = 0; row < array.length; row++) {
            if (array[row][column].trim().equals(valor)) {
                break;
            } // end if
        } // end for

        return row;
    } // end seek

    /**
     * Este método busca dentro de un arreglo y retorna el índice en donde se encuentra el
     * valor buscado. En caso de no existir el valor especificado devolverá un -1.
     *
     * @param valor String valor a buscar
     * @Autor: Bosco Garita 05/07/2014.
     * @param array String[] arreglo en donde se realizará la búsqueda
     * @return int (Fila en donde se encontró el valor) -1 si no existe.
     */
    public static int seek(String[] array, String valor) {
        // No se admiten valores nulos y por lo tanto no se hace la validación
        // para que de error y corregir el programa que invoque este método.
        valor = valor.trim();

        int row = -1; // Es necesario inicializarlo así.

        for (row = 0; row < array.length - 1; row++) {
            if (array[row].trim().equals(valor)) {
                break;
            } // end if
        } // end for

        return row;
    } // end seek

    /**
     * Rellena a la izquierda con el caracter que reciba en el parámetro caracter
     *
     * @Autor: Bosco Garita 01/01/2010.
     * @param cadena string base
     * @param caracter caracter de relleno
     * @param longitud longitud total de la cadena a devolver
     * @return Devuelve una cadena rellena a la izquierda
     */
    public static String lpad(String cadena, String caracter, int longitud) {
        if (cadena == null) {
            cadena = "";
        } // end if
        if (caracter == null) {
            caracter = "0";     // Valor default
        } // end if

        String cadenaOriginal = cadena;

        cadena = "";

        int longitudOriginal = cadenaOriginal.length();

        if (longitud <= longitudOriginal) {
            return cadenaOriginal;
        } // end if

        int i = 1;
        while (i <= (longitud - longitudOriginal)) {
            cadena += caracter;
            i++;
        } // end while

        cadena = cadena += cadenaOriginal;

        return cadena;
    } // end lpad

    /**
     * Autor: Bosco Garita 07/02/2011. Método sobrecargado para lpad de tipo string
     *
     * @param numero entero a rellenar
     * @param caracter caracter de relleno
     * @param longitud tamaño del string relleno
     * @return cadena rellena a la izquierda
     */
    public static String lpad(int numero, String caracter, int longitud) {
        return lpad(String.valueOf(numero), caracter, longitud);
    } // end lpad

    /**
     * Rellena a la derecha con el caracter que reciba en el parámetro caracter
     *
     * @autor: Bosco Garita 01/01/2010.
     * @param cadena string base
     * @param caracter caracter de relleno
     * @param longitud longitud total de la cadena a devolver
     * @return Devuelve una cadena rellena a la derecha
     */
    public static String rpad(String cadena, String caracter, int longitud) {
        if (cadena == null) {
            cadena = "";
        } // end if
        if (caracter == null) {
            caracter = "0"; // Valor default
        } // end if

        String cadenaOriginal = cadena;
        int longitudOriginal = cadena.length();

        if (longitud <= longitudOriginal) {
            longitud = longitudOriginal + 1;
        } // end if

        int i = 1;
        while (i <= (longitud - longitudOriginal)) {
            cadena += caracter;
            i++;
        } // end while

        cadenaOriginal = cadenaOriginal += cadena;

        return cadena;
    } // end rpad

    /**
     * Autor: Bosco Garita 07/02/2011. Método sobrecargado para rpad de tipo string
     *
     * @param numero entero a rellenar
     * @param caracter caracter de relleno
     * @param longitud tamaño del string relleno
     * @return cadena rellena a la izquierda
     */
    public static String rpad(int numero, String caracter, int longitud) {
        return rpad(String.valueOf(numero), caracter, longitud);
    } // end lpad

    /**
     * Rellena a la izquierda y a la derecha con el caracter que reciba en el parámetro
     * caracter
     *
     * @autor: Bosco Garita 12/09/2011.
     * @param cadena string base
     * @param caracter caracter de relleno
     * @param longitud longitud total de la cadena a devolver
     * @return Devuelve una cadena rellena a la izquierda y a la derecha
     */
    public static String cpad(String cadena, String caracter, int longitud) {
        if (cadena == null) {
            cadena = "";
        }

        if (caracter == null) {
            caracter = "0";     // Valor default
        } // end if

        cadena = cadena.trim();
        if (longitud <= cadena.length()) {
            return cadena;
        }

        int leftPad, rightPad;
        leftPad = (longitud - cadena.length()) / 2;
        rightPad = longitud - cadena.length() - leftPad;

        StringBuilder izquierda = new StringBuilder("");
        for (int i = 0; i < leftPad; i++) {
            izquierda.append(caracter);
        }

        StringBuilder derecha = new StringBuilder("");
        for (int i = 0; i < rightPad; i++) {
            derecha.append(caracter);
        }

        return izquierda.toString() + cadena + derecha.toString();
    } // end cpad

    /**
     * Autor: Bosco Garita 28/11/2009 Buscar en un objeto de tipo JComboBox si existe el
     * objeto recibido en el parámetro valor. Si la búsqueda tiene éxito el método dejará
     * seleccionado el Item encontrado y devolverá true, de lo contrario no seleccionará
     * nada y devolverá false.
     *
     * @param combo Objeto de tipo JComboBox en donde se realizará la búsqueda
     * @param buscar Object texto a buscar.
     * @return true = el objeto fue encontrado, false no fue encontrado
     */
    public static boolean seek(JComboBox combo, Object buscar) {
        boolean existe = false;
        buscar = buscar.toString().trim();
        for (int row = 0; row < combo.getItemCount(); row++) {
            if (combo.getItemAt(row).toString().trim().equals(buscar)) {
                combo.setSelectedIndex(row);
                existe = true;
                break;
            } // end if
        } // end for
        return existe;
    } // end seek

    /**
     * Redondea un número a 5 ó 10. Depende cuál de estos enteros es el más cercano. Por
     * ejemplo: 122 se redondeará a 120; 123 se redondeará a 125; 127 se redondeará a 125;
     * 128 se redondeará a 130. Si el valor recibido viene con decimales éstos serán
     * ignorados tanto para la evaluación del redondeo como para el resultado final.
     *
     * @param pNumero String que contiene el valor a redondear.
     * @return String que contiene el valor redondeado. Si el valor recibido es un entero
     * entonces el String devuelto también será un entero.
     */
    public static String redondearA5(String pNumero) {
        // Convierto el parámetro recibido a Double.  Esto garantiza que aunque
        // sea un entero se le agregará el punto decimal y al menos un cero.
        // Esto es necesario para realizar el replace y el split.
        Double lnNumero = Double.valueOf(pNumero);
        boolean esNegativo = lnNumero < 0;
        if (esNegativo) {
            lnNumero = Math.abs(lnNumero);
        } // end if
        String devolver = pNumero;
        // Si el número es menor a 5 no se redondea.
        if (lnNumero < 5) {
            return devolver;
        } // end if

        pNumero = String.valueOf(lnNumero);
        // Cambio el punto decimal por un caracter que sea reconocido por el
        // método split.
        pNumero = pNumero.replace(".", "/");
        // Creo un arreglo String con dos filas; entero y decimales.
        String[] aNumero = pNumero.split("/");
        // Obtengo el último dígito del entero.
        String ultimoDigito = aNumero[0].substring(aNumero[0].length() - 1);
        // Convierto a Double el último dígito del entero
        Double valor = Double.valueOf(ultimoDigito);
        // Proceso de redondeo a 5 ó 10
        if (Integer.valueOf(ultimoDigito) > 5) {
            if (10 - Integer.valueOf(ultimoDigito) <= 2.5) {
                lnNumero = lnNumero + (10 - valor);
            } else {
                lnNumero = lnNumero - valor + 5;
            } // end if
        } else {
            if (5 - Integer.valueOf(ultimoDigito) <= 2.5) {
                lnNumero = lnNumero + (5 - valor);
            } else {
                lnNumero = lnNumero - valor;
            } // end if
        } // end if
        if (esNegativo) {
            lnNumero *= -1;
        } // end if
        devolver = String.valueOf(lnNumero);
        // Ahora se eliminan los decimales también (Bosco 01/07/2010)
        //if (esEntero){
        devolver = String.valueOf(lnNumero.intValue());
        //} // end if
        return devolver;
    } // end redondearA5

    /**
     * Redondear un número de tipo double a un número determinado de decimales. El
     * redondeo se hará al dígito más cercano utilizando tres métodos: Hacia arriba
     * (HALF_UP), hacia abajo (HALF_DOWN) o dinámico (HALF_EVEN). En una ejecución
     * sucesiva de redondeos el que tiene mayor precision es el dinámico.
     *
     * @author: Bosco Garita 03/05/2013
     * @param numero double número a redondear
     * @param decimales int número de posiciones decimales que se usarán en el redondeo
     * @param metodo int método de redondeo (1=Arriba, 2=Abajo, 3=Dinámico).
     * @return double Número redondeado.
     */
    public static double redondear(double numero, int decimales, int metodo) {
        String valor = numero + "";
        BigDecimal big = new BigDecimal(valor);

        /*
         * Establecer el valor por defecto de método
         */
        if (metodo < 1 || metodo > 3) {
            metodo = 3;
        } // end if

        /*
         * Cargar el redondeo según las constantes de redondeo de BigDecimal
         */
        switch (metodo) {
            case 1:
                big = big.setScale(decimales, RoundingMode.HALF_UP);
                break;
            case 2:
                big = big.setScale(decimales, RoundingMode.HALF_DOWN);
                break;
            default:
                big = big.setScale(decimales, RoundingMode.HALF_EVEN);
        } // end switch

        return big.doubleValue();
    } // end redondear

    /**
     *
     * Redondear un float a un número específico de decimales. Ver más detalles en
     * redondear(double numero, int decimales, int metodo)
     *
     * @param numero float número a redondear
     * @param decimales int número de decimales a usar en el redondeo
     * @param metodo int (1=Arriba, 2=Abajo, 3=Dinámico)
     * @return float número redondeado
     */
    public static float redondear(float numero, int decimales, int metodo) {
        float devolver;
        double n = numero;
        n = redondear(n, decimales, metodo);
        devolver = Float.parseFloat(n + "");
        return devolver;
    } // end redondear

    /**
     * Convertir un número de mes a letras. Este número va de acuerdo a la clase Calendar;
     * es decir enero=0
     *
     * @param pMes int Número de mes.
     * @return String nombre del mes en español (Enero, Febrero, Marzo...)
     */
    public static String mesLetras(int pMes) {
        String mes;
        switch (pMes) {
            case Calendar.JANUARY:
                mes = "Enero";
                break;
            case Calendar.FEBRUARY:
                mes = "Febrero";
                break;
            case Calendar.MARCH:
                mes = "Marzo";
                break;
            case Calendar.APRIL:
                mes = "Abril";
                break;
            case Calendar.MAY:
                mes = "Mayo";
                break;
            case Calendar.JUNE:
                mes = "Junio";
                break;
            case Calendar.JULY:
                mes = "Julio";
                break;
            case Calendar.AUGUST:
                mes = "Agosto";
                break;
            case Calendar.SEPTEMBER:
                mes = "Setiembre";
                break;
            case Calendar.OCTOBER:
                mes = "Octubre";
                break;
            case Calendar.NOVEMBER:
                mes = "Noviembre";
                break;
            case Calendar.DECEMBER:
                mes = "Diciembre";
                break;
            default:
                mes = "";
        } // end switch
        return mes;
    } // end mesLetras

    /**
     * Retornar el día de la semana end español (Domingo, Lunes, Martes...)
     *
     * @param pDia
     * @return
     */
    public static String diaLetras(int pDia) {
        String dia;
        switch (pDia) {
            case Calendar.SUNDAY:
                dia = "Domingo";
                break;
            case Calendar.MONDAY:
                dia = "Lunes";
                break;
            case Calendar.TUESDAY:
                dia = "Martes";
                break;
            case Calendar.WEDNESDAY:
                dia = "Miércoles";
                break;
            case Calendar.THURSDAY:
                dia = "Jueves";
                break;
            case Calendar.FRIDAY:
                dia = "Viernes";
                break;
            case Calendar.SATURDAY:
                dia = "Sábado";
                break;
            default:
                dia = "";
        } // end switch
        return dia;
    } // end diaLetras

    /**
     * Bosco Garita 17/05/2010 11:54 pm Totalizar los valores de una columna en una tabla.
     * Esta columna puede ser numérica o String de números. Si es un String no importa si
     * viene formateada.
     *
     * @param t Tabla
     * @param col Número de columna a totalizar
     * @return Number
     * @throws java.text.ParseException
     */
    public static Number sum(JTable t, int col) throws Exception {
        Number suma = 0;
        String valor;
        //Long sumaInt = 0L;       // Aquí se sumarán todos los tipos de entero
        Double sumaDouble = 0.00; // Aquí todos los de punto flotante

        // Obtener el modelo de la tabla
        DefaultTableModel dtm = (DefaultTableModel) t.getModel();
        int rows = dtm.getRowCount(); // Número de filas en la tabla

        for (int i = 0; i < rows; i++) {
            if (t.getValueAt(i, col) == null) {
                continue;
            } // end if

            if (t.getValueAt(0, col) instanceof Float
                    || t.getValueAt(0, col) instanceof Double
                    || t.getValueAt(0, col) instanceof String
                    || t.getValueAt(0, col) instanceof Byte
                    || t.getValueAt(0, col) instanceof Short
                    || t.getValueAt(0, col) instanceof Integer
                    || t.getValueAt(0, col) instanceof Long) {
                valor = Ut.quitarFormato(t.getValueAt(i, col).toString());
                sumaDouble += Double.parseDouble(valor);
            } // end if
            suma = (Number) sumaDouble;
        } // end for

//        if (t.getValueAt(0, col) instanceof Float
//                || t.getValueAt(0, col) instanceof Double
//                || t.getValueAt(0, col) instanceof String) {
//            // Recorro la tabla sumando los valores
//            for (int i = 0; i < rows; i++) {
//                if (t.getValueAt(i, col) != null) {
//                    valor = Ut.quitarFormato(t.getValueAt(i, col).toString());
//                    sumaFloat += Double.parseDouble(valor);
//                }// end if
//            } // end for
//            suma = (Number) sumaFloat;
//        } else if (t.getValueAt(0, col) instanceof Byte
//                || t.getValueAt(0, col) instanceof Short
//                || t.getValueAt(0, col) instanceof Integer
//                || t.getValueAt(0, col) instanceof Long) {
//            // Recorro la tabla sumando los valores
//            for (int i = 0; i < rows; i++) {
//                if (t.getValueAt(i, col) != null) {
//                    valor = Ut.quitarFormato(t.getValueAt(i, col).toString());
//                    sumaInt = +Long.parseLong(valor);
//                } // end if
//            } // end for
//            suma = (Number) sumaInt;
//        }
//        if (suma == null) {
//            suma = 0;
//        } // end if
        return suma;
    } // end sum

    /**
     * Bosco Garita 04/02/2016 11:19 am Totalizar los valores de dos columnas según el
     * operador matemático recibido por parámetro. Estas columnas pueden ser numéricas o
     * String de números. Si son String no importa si vienen formateadas. </br>
     * NOTA: solo admite las cuatro operaciones básicas (+,-,*,/)
     *
     * @param t JTable Tabla que contiene los datos
     * @param col1 int Número de columna que tiene el valor que formará la parte
     * izquierada de la expresión
     * @param operator Strihg Operador que indicará el resultado a obtener
     * @param col2 int Número de columna que tiene el valor que formará la parte derecha
     * de la expresión
     * @return Number Sumatoria del resultado de la expresión formada entre col1, operator
     * y col2
     * @throws java.text.ParseException
     */
    public static Number sum(JTable t, int col1, char operator, int col2) throws Exception {
        String valor;           // Tendrá el valor de las dos columnas cada una a su tiempo.
        Double valorIz;         // Valor de la izquierda de la expresión
        Double valorDe;         // Valor de la derecha de la expresión
        Double total = 0.00;    // Resultado de la expresión
        Number suma;            // Tendrá el resultado convertido de la suma de expresiones

        // Obtener el modelo de la tabla
        DefaultTableModel dtm = (DefaultTableModel) t.getModel();
        int rows = dtm.getRowCount(); // Número de filas en la tabla

        // Recorro la tabla sumando los valores
        for (int i = 0; i < rows; i++) {
            if (t.getValueAt(i, col1) == null) {
                continue;
            }

            valor = Ut.quitarFormato(t.getValueAt(i, col1).toString());
            valorIz = Double.parseDouble(valor);

            valor = Ut.quitarFormato(t.getValueAt(i, col2).toString());
            valorDe = Double.parseDouble(valor);

            switch (operator) {
                case '+':
                    valorIz = valorIz + valorDe;
                    break;
                case '-':
                    valorIz = valorIz - valorDe;
                    break;
                case '*':
                    valorIz = valorIz * valorDe;
                    break;
                case '/':
                    valorIz = valorIz / valorDe;
                    break;
            } // end switch

            total += valorIz;
        } // end for

        suma = (Number) total;

        return suma;
    } // end sum

    /**
     * Bosco Garita 06/08/2015 10:54 pm Totalizar los valores de una columna en una tabla
     * filtrando los datos en base al valor que contenga una columna específica. La
     * columna a sumar puede ser numérica o String de números. Si es un String no importa
     * si viene formateada.
     *
     * @param t JTable Tabla
     * @param col int Número de columna a totalizar
     * @param filterValue Object valor a utilizar como filtro
     * @param filterCol int número de columna que se usará en el filtro
     * @return Number total obtenido
     * @throws java.text.ParseException
     */
    public static Number sum(JTable t, int col, Object filterValue, int filterCol) throws Exception {
        Number suma = null; // Tendrá el resultado convertido
        String valor;
        Long sumaInt = 0L;       // Aquí se sumarán todos los tipos de entero
        Double sumaFloat = 0.00; // Aquí todos los de punto flotante
        Object filter;           // Aqui se carga el valor de la celda filtro

        // Obtener el modelo de la tabla
        DefaultTableModel dtm = (DefaultTableModel) t.getModel();
        int rows = dtm.getRowCount(); // Número de filas en la tabla
        if (t.getValueAt(0, col) instanceof Float
                || t.getValueAt(0, col) instanceof Double
                || t.getValueAt(0, col) instanceof String) {

            // Recorro la tabla sumando los valores
            for (int i = 0; i < rows; i++) {
                // Si el valor de la columna filtro no es igual al valor del
                // filtro, entonces no hago nada.
                filter = t.getValueAt(i, filterCol);

                if (filter == null || !filter.equals(filterValue)) {
                    continue;
                } // end if

                if (t.getValueAt(i, col) != null) {
                    valor = Ut.quitarFormato(t.getValueAt(i, col).toString());
                    sumaFloat += Double.parseDouble(valor);
                }// end if
            } // end for
            suma = (Number) sumaFloat;
        } else if (t.getValueAt(0, col) instanceof Byte
                || t.getValueAt(0, col) instanceof Short
                || t.getValueAt(0, col) instanceof Integer
                || t.getValueAt(0, col) instanceof Long) {

            // Recorro la tabla sumando los valores
            for (int i = 0; i < rows; i++) {
                // Si el valor de la columna filtro no es igual al valor del
                // filtro, entonces no hago nada.
                filter = t.getValueAt(i, filterCol);

                if (filter == null || !filter.equals(filterValue)) {
                    continue;
                } // end if

                if (t.getValueAt(i, col) != null) {
                    valor = Ut.quitarFormato(t.getValueAt(i, col).toString());
                    sumaInt = +Long.parseLong(valor);
                } // end if
            } // end for
            suma = (Number) sumaInt;
        }
        if (suma == null) {
            suma = 0;
        } // end if

        return suma;
    } // end sum

    /**
     * Devuelve varias características que son prácticas a la hora de desarrollar
     * aplicaciones. Algunas de ellas son de uso exclusivo en Windows XP.
     *
     * @param prop Característica (ver las constantes de Utilitarios)
     * @return String característica deseada
     */
    public static String getProperty(int prop) {
        String name = null;
        switch (prop) {
            case USER_NAME:
                name = System.getProperty("user.name");
                break;
            case USER_DIR:
                name = System.getProperty("user.dir");
                break;
            case USER_HOME:
                name = System.getProperty("user.home");
                break;
            case TMPDIR:
                name = System.getProperty("java.io.tmpdir");
                break;
            case OS_NAME:
                name = System.getProperty("os.name");
                break;
            case OS_VERSION:
                name = System.getProperty("os.version");
                break;
            case FILE_SEPARATOR:
                name = System.getProperty("file.separator");
                break;
            case PATH_SEPARATOR:
                name = System.getProperty("path.separator");
                break;
            case LINE_SEPARATOR:
                name = System.getProperty("line.separator");
                break;
            case WINDIR:
                if (System.getProperty("os.name").equalsIgnoreCase("Windows XP")) {
                    name = System.getenv("windir");
                } // end if
                break;
            case SYSTEM32:
                if (System.getProperty("os.name").equalsIgnoreCase("Windows XP")) {
                    name = System.getenv("windir") + "\\system32";
                } // end if
                break;
            case COMPUTERNAME:
                name = System.getenv("COMPUTERNAME");
                break;
            case PROCESSOR_IDENTIFIER:
                name = System.getenv("PROCESSOR_IDENTIFIER");
                break;
            case JAVA_VERSION:
                name = System.getenv("java.version");
                break;
        } // end switch
        return name;
    } // end getProperty

    /**
     * @throws java.sql.SQLException
     * @throws Exceptions.EmptyDataSourceException
     * @Author: Bosco Garita 04/01/2011. Carga un comboBox con los datos de un ResultSet
     * @param combo comboBox que se llenará
     * @param rs ResultSet con los datos para llenar el combo
     * @param col número de columna (del ResultSet) que se usará en el combo
     * @param replace true=sustituye los datos del combo, false=los agrega
     * @return true=el proceso fue exitoso, false=el proceso falló Nota 1: el ResultSet
     * que reciba este método debe venir con el atributo de
     * ResultSet.TYPE_SCROLL_SENSITIVE. Nota 2: Si el parámetro replace viene en true debe
     * asegurarse de que el evento ActionPerformed o algún otro que esté asociado al
     * comboBox no se dispare durante la ejecución de este método porque causará un error
     * de Null Pointer.
     */
    public static boolean fillComboBox(
            javax.swing.JComboBox combo,
            ResultSet rs,
            int col,
            boolean replace) throws SQLException, EmptyDataSourceException {

        boolean datosCargados = false;
        int registros;

        // Asumo la columna uno como valor predeterminado
        if (col <= 0) {
            col = 1;
        } // end if

        if (rs == null || !rs.first()) {
            throw new EmptyDataSourceException("Fuente de datos vacía");
        } // end if

        if (replace) {
            combo.removeAllItems();
        } // end if

        rs.last();
        registros = rs.getRow();
        for (int i = 1; i <= registros; i++) {
            rs.absolute(i);
            combo.addItem(rs.getString(col));
        } // end for

        return datosCargados;

    } // end fillComboBox

    /**
     * Autor: Bosco Garita 08/02/2011 10:48 p.m.Objet: Mover el puntero a una posición
     * relativa dentro del RS
     *
     * @param r ResultSet, debe venir con movilidad
     * @param pos Posición a la que se moverá el puntero
     * @return boolean true = Fue exitoso, false = No lo fue
     * @throws java.sql.SQLException
     */
    public static boolean goRecord(ResultSet r, int pos) throws SQLException {
        boolean exito = false;
        if (r == null) {
            return exito;
        } // end if

        switch (pos) {
            case BEFORE_FIRST:
                exito = true;
                r.beforeFirst();
                break;
            case FIRST:
                exito = r.first();
                break;
            case LAST:
                exito = r.last();
                break;
            case NEXT:
                exito = r.next();
                break;
            case PREVIOUS:
                exito = r.previous();
                break;
            case AFTER_LAST:
                exito = true;
                r.afterLast();
                break;
            case ABSOLUTE:
                exito = r.absolute(pos);
                break;
            default:
                exito = false;
        } // end switch

        return exito;
    } // end goRecord

    /**
     * Autor: Bosco Garita 17/04/2011 Este método es similar a RECNO() en VisualFox,
     * devuelve el número de registro actual dentro de un ResultSet. Si el RS no tiene
     * datos o está nulo devuelve cero
     *
     * @param r ResultSet a verificar
     * @return int número de registro actual en el RS
     */
    public static int recNo(ResultSet r) throws SQLException {
        int registro = 0;

        if (r != null) {
            registro = r.getRow();
        } // end if

        return registro;
    } // end recNo

    /**
     * Autor: Bosco Garita 12/08/2011 Este método es similar a RECCOUNT() en VisualFox,
     * devuelve el número de registros dentro de un ResultSet. Si el RS no tiene datos o
     * está nulo devuelve cero
     *
     * @param r ResultSet a verificar
     * @return int número de registro actual en el RS
     * @throws SQLException
     */
    public static int recCount(ResultSet r) throws SQLException {
        int registro = 0;

        if (r != null) {
            int actual = r.getRow();
            r.last();
            registro = r.getRow();
            if (actual != 0) {
                r.absolute(actual);
            } else {
                r.first();
            }
        } // end if

        return registro;

    } // end recCount

    /**
     * Este método necesita que venga la palabra AS como parte de la expresión si se trata
     * de una expresión pero si solo es un campo no la requiere.
     *
     * @author: Bosco Garita 07/05/2011
     * @param expresion
     * @return String alias
     */
    public static String getAlias(String expresion) {
        if (expresion == null) {
            return "";
        } // end if

        String alias = "";
        boolean isDistintc;
        int aliasPos;
        expresion = expresion.toUpperCase();

        /* Bosco agregado 18/02/2013.
         * Si viene la palabra reservada DISTINCT entonces el alias debe 
         * considerarse después del segundo blanco.
         * Para lograr este efecto sin afectar la funcionalidad de abajo
         * elimino la palabra reservada.
         */
        isDistintc = expresion.contains("DISTINCT");

        if (isDistintc) {
            int posBlank = expresion.indexOf(" ");
            expresion = expresion.substring(posBlank).trim();
        } // end if
        // Fin Bosco agregado 18/02/2013.

        // Toda expresión contiene al menos un espacio en blanco.
        if (!expresion.contains(" ")) {
            alias = expresion;
        } // end if

        if (!expresion.equals(alias)) {
            // Analizo la expresión para ver si contiene un alias.
            // Si no es una expresión entonces el alias será el nombre del campo
            aliasPos = expresion.indexOf("AS");
            alias = expresion.substring(aliasPos + 2).trim();
        } // end if

        return alias;
    } // end getAlias

    /**
     * Este método revisa algunas características sobre inyección de código. Está diseñado
     * para trabajar sobre MySQL únicamente pero responde a la mayoría de SQL stándar.
     *
     * @param sqlSent
     * @return true=Hay inyección, false=no hay
     * @throws logica.utilitarios.SQLInjectionException
     */
    public static boolean isSQLInjection(String sqlSent) throws SQLInjectionException {
        boolean inyectado;
        // Pasar todo a mayúcula para facilitar la revisión
        String s = sqlSent.toUpperCase();

        // Hago una primera revisión de inyección por igualación.
        inyectado = injectionByOperator(s);

        if (inyectado) {
            throw new SQLInjectionException(
                    "Se ha detectado una posible inyección de código.\n"
                    + "La sentencia SQL no se ejecutará.");
        } // end if

        // También elimino todos los espacios en blanco para buscar inyección.
        s = s.replaceAll(" ", "");
        // Buscar secuencias peligrosas que permiten la inyección de código.
        inyectado
                = s.contains(";")
                || // Finaliza un query y abre uno nuevo
                s.contains("LIKE'%'")
                || // Como lo que sea
                s.contains("'--")
                || // Comentarios de líea
                (s.contains("*/") && s.contains("*/"))
                || // Comentarios de bloque
                s.contains("')OR1=")
                || // Evitar que se agregue una expresión
                s.contains("CHAR(0X66)")
                || // Evitar expresiones demasiado largas
                s.contains("CHAR(0X5045)")
                || // Evitar expresiones demasiado largas
                s.contains("0X50+0X45")
                || // Evitar expresiones demasiado largas
                s.contains("LOAD_FILE(")
                || // No se permite el uso de Load_File()
                s.contains("LIMIT0")
                || // Evitar LIMIT 0 (MySQL).
                s.contains("TOP0")
                || // Evitar TOP 0 (MSSQL).
                s.contains("BENCHMARK(")
                || // No se permite la función BENCHMARK()
                s.contains("VERSION(") // No se permite la función VERSION()
                ;
        if (inyectado) {
            throw new SQLInjectionException(
                    "Se ha detectado una posible inyección de código.\n"
                    + "La sentencia SQL no se ejecutará.");
        } // end if
        // Incorporar casos como este: ' or 1=1
        // En este caso se trata de detectar expresiones que siempre evalúen
        // a true.
        // Evaluar la posibilidad de leer secuencias de caracteres desde una
        // tabla de manera que se pueda ir alimentando constantemente.
        return inyectado;
    } // end isSQLInjection

    /**
     * @author: Bosco Garita 07/02/2012 Este método convierte una fecha y hora a String.
     * @param dateTime long (fecha y hora)
     * @return String con el formato ""dd/mm/aaaa hh:mm:ss a""
     */
    public static String ttoc(long dateTime) {
        Date d = new Date();
        d.setTime(dateTime);
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        return f.format(d);
    } // end ttoc

    /**
     * @author: Bosco Garita 03/01/2015 Este método convierte una fecha y hora a fecha sin
     * hora.
     * @param dateTime long (fecha y hora)
     * @return Date fecha
     */
    public static Date ttod(long dateTime) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(dateTime);
        String fecha
                = cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
        return Ut.ctod(fecha);
    } // end ttod

    /**
     * Método para retornar una fecha con formato String dd/mm/aaaa hh:mm:ss
     *
     * @author Bosco Garita 25/05/2016
     * @param cal Calendar con la fecha establecida
     * @return String fecha dd/mm/aaaa hh:mm:ss
     */
    public static String ttoc(Calendar cal) {
        int dia, mes, año;
        int hora, min, seg;
        año = cal.get(Calendar.YEAR);
        mes = cal.get(Calendar.MONTH) + 1;
        dia = cal.get(Calendar.DAY_OF_MONTH);
        hora = cal.get(Calendar.HOUR);
        min = cal.get(Calendar.MINUTE);
        seg = cal.get(Calendar.SECOND);
        return dia + "/" + mes + "/" + año + " " + hora + ":" + min + ":" + seg;
    } // end ttoc

    /**
     * Autor: Bosco Garita 29/04/2012 Este método limpia todas las celdas de una JTable.
     *
     * @param tabla
     */
    public static void clearJTable(JTable tabla) {
        int rowCount = tabla.getModel().getRowCount();
        int columnCount = tabla.getColumnModel().getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                tabla.setValueAt(null, i, j);
            } // end for
        } // end for
    } // end clearJTable

    /**
     * @author Bosco Garita 28/10/2012 Cuenta la cantidad filas que tienen una columna de
     * tipo check seleccionada.
     * @param table JTable a revisar
     * @param column número de columna a revisar
     * @return checked (Cantidad de registros con check)
     */
    public static int countChecked(JTable table, int column) {
        int checked = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getValueAt(i, column) != null
                    && Boolean.valueOf(table.getValueAt(i, column).toString())) {
                checked++;
            } // end if
        } // end for
        return checked;
    } // end countChecked

    /**
     * @since 14/02/2013 Bosco Garita Equivalente a AT() de Visual FoxPro. Devuelve un
     * entero indicando la posición en donde se encuentra la subcadena a buscar. En caso
     * de no existir devuelve -1
     * @param cadena String en donde se realizará la búsqueda
     * @param subcadena String que se buscará
     * @return int primera posición encontrada
     */
    public static int getPosicion(String cadena, String subcadena) {
        return getPosicion(cadena, subcadena, 1);
    } // end getPosicion

    /**
     * @since 14/02/2013 Bosco Garita Equivalente a AT() de Visual FoxPro. Devuelve un
     * entero indicando la posición en donde se encuentra la subcadena a buscar
     * considerando el número de ocurrencia. En caso de no existir devuelve -1
     * @param cadena String en donde se realizará la búsqueda
     * @param subcadena String que se buscará
     * @param ocurrencia int número de ocurrencia
     * @return int primera posición encontrada
     */
    public static int getPosicion(String cadena, String subcadena, int ocurrencia) {
        int recorrido = 0;
        int pos = -1;
        int posicion = -1;

        // Si lo que se busca es la primera ocurrencia entonces uso la 
        // función nativa de Java aún cuando el while sirve para cualquier caso.
        if (ocurrencia == 1) {
            return cadena.indexOf(subcadena);
        } // end if

        while (recorrido < ocurrencia) {
            posicion = cadena.indexOf(subcadena, pos + 1);
            pos = posicion;
            recorrido++;
        } // end while

        return posicion;
    } // end getPosicion

    /**
     * @since 14/02/2013 Bosco Garita Equivalente a ATC() de Visual FoxPro. Devuelve un
     * entero indicando la posición en donde se encuentra la subcadena a buscar. En caso
     * de no existir devuelve -1. No hace distinción de mayúscula y minúscula.
     * @param cadena String en donde se realizará la búsqueda
     * @param subcadena String que se buscará
     * @return int primera posición encontrada
     */
    public static int getPosicionIgnoreCase(String cadena, String subcadena) {
        return getPosicion(cadena.toUpperCase(), subcadena.toUpperCase(), 1);
    } // end getPosicionIgnoreCare

    /**
     * @since 14/02/2013 Bosco Garita Equivalente a ATC() de Visual FoxPro. Devuelve un
     * entero indicando la posición en donde se encuentra la subcadena a buscar. En caso
     * de no existir devuelve -1. No hace distinción de mayúscula y minúscula.
     * @param cadena String en donde se realizará la búsqueda
     * @param subcadena String que se buscará
     * @param ocurrencia int número de ocurrencia
     * @return int primera posición encontrada
     */
    public static int getPosicionIgnoreCase(String cadena, String subcadena, int ocurrencia) {
        return getPosicion(cadena.toUpperCase(), subcadena.toUpperCase(), ocurrencia);
    } // end getPosicionIgnoreCare

    /**
     *
     * @param cadena String en donde se realizará la búsqueda
     * @param subcadena String que se buscará
     * @return int primera posición encontrada
     */
    public static int AT(String cadena, String subcadena) {
        return getPosicion(cadena, subcadena);
    } // end AT

    public static int AT(String cadena, String subcadena, int ocurrencia) {
        return getPosicion(cadena, subcadena, ocurrencia);
    } // end AT

    public static int ATC(String cadena, String subcadena) {
        return getPosicionIgnoreCase(cadena, subcadena);
    } // end AT

    public static int ATC(String cadena, String subcadena, int ocurrencia) {
        return getPosicionIgnoreCase(cadena, subcadena, ocurrencia);
    } // end AT

    /**
     * @author Bosco Garita 18/02/2013 Quita todos los caracteres de una cadena dejando
     * solo los dígitos.
     * @param valor String que será examinada
     * @return Number valor numérico de la cadena recibida.
     */
    public static Number quitarCaracteres(String valor) {
        Number numero = 0;
        char tempChar;
        String tempString = "";
        for (int i = 0; i < valor.length(); i++) {
            tempChar = valor.charAt(i);
            if (Character.isDigit(tempChar)) {
                tempString = tempString + tempChar;
            } // end if
        } // end for
        if (tempString.length() > 0) {
            numero = Integer.parseInt(tempString);
        } // end if
        return numero;
    } // end quitarCaracteres

    /**
     * @author Bosco Garita 20/07/2019 Quita todos los caracteres de una cadena dejando
     * sólo los dígitos y el primer punto.
     * @param valor String que será examinada
     * @param separadorDecimal String indica el separador decimal que pueda traer la
     * cadena.
     * @return Number valor numérico de la cadena recibida.
     */
    public static Number quitarCaracteres(String valor, String separadorDecimal) {
        Number numero = 0;
        char tempChar;
        String tempString = "";
        boolean separador = false; // Se vuelve true cuando se detecta el separador decimal.
        for (int i = 0; i < valor.length(); i++) {
            tempChar = valor.charAt(i);

            if (Character.isDigit(tempChar) || (!separador && separadorDecimal.charAt(0) == tempChar)) {
                tempString = tempString + tempChar;
                if (separadorDecimal.charAt(0) == tempChar) {
                    separador = true;
                } // end if
            } // end if

        } // end for
        if (tempString.length() > 0) {
            numero = Double.parseDouble(tempString);
        } // end if
        return numero;
    } // end quitarCaracteres

    /**
     * Este método verifica expresiones lógicas que siempre den positivo pero se limita al
     * operador igual que (=) precedido de un OR.
     *
     * @author Bosco Garita Azofeifa 13/07/2013
     * @param query String cadena de texto a analizar. Debe venir en mayúscula.
     * @return boolean true=Existe inyección, false=No existe.
     */
    public static boolean injectionByOperator(String query) {
        boolean injected = false;
        int pos, pos2;
        String exp1, exp2;

        // Verifico si existe al menos un OR.  Si no existe ninguno entonces
        // no continúo con la revisión ya que una forma de inyección de código
        // por operador es inmediatamente después del OR.
        // Nota: la búsqueda del OR se hace con espacio a ambos lados para evitar
        // que se confunda con cualquier otra palabra que lleve OR como ORDER BY
        if (!query.contains(" OR ")) {
            return injected;
        } // end if

        // Elimino todo el texto hasta el where inclusive (si existe).
        pos = Ut.AT(query, "WHERE");
        if (pos >= 0) {
            query = query.substring(pos + 6).trim();
        } // end if

        // Elimino todo el texto hasta el OR inclusive (si existe).
        pos = Ut.AT(query, "OR");
        if (pos >= 0) {
            query = query.substring(pos + 3).trim();
        } // end if

        pos = Ut.AT(query, "=");

        // Si no existe el símbolo no hay posibilidad de inyección.
        if (pos < 0) {
            return injected;
        }

        exp1 = query.substring(0, pos).trim();
        pos2 = Ut.AT(exp1, " ");
        while (pos2 >= 0) {
            if (Ut.AT(exp1, " ") >= 0) {
                exp1 = exp1.substring(pos2).trim();
            } // end if
            pos2 = Ut.AT(exp1, " ");
        } // end while
        //exp2 = "";
        query = query.substring(pos + 1).trim();
        exp2 = query;
        pos = Ut.AT(query, " ");
        if (pos > 0) {
            exp2 = query.substring(0, pos).trim();
            query = query.substring(pos).trim();
        } // end if

        if (exp1.equals(exp2)) {
            injected = true;
        }

        // Llamada recursiva para continuar con el resto de la cadena
        if (!injected) {
            injected = injectionByOperator(query);
        }

        //System.out.println("Exp1=" + exp1 + ", Exp2=" + exp2);
        //System.out.println(injected);
        return injected;
    } // end injectionByOperator

    /**
     * Devuelve una cadena de texto formateada tipo título (cada palabra inicia con
     * mayúscula y el resto queda en minúscula)
     *
     * @param s String texto a formatear
     * @return texto String texto formateado
     */
    public static String tipoTitulo(String s) {
        String texto;
        texto = s.trim().toLowerCase();
        String[] tokens = texto.split("\\s");
        texto = "";
        for (String word : tokens) {
            if (word.isEmpty()) {
                continue;
            } // end if
            texto = texto + word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
        } // end for

        return texto.trim();
    } // end tipoTitulo

    /**
     * Este método redefine el tamaño de una JTable. Si el número de columnas/filas que
     * reciba por parámetro es negativo entonces le restará ese número al que ya tiene la
     * tabla pero si es positivo entonces se lo incrementa.
     *
     * @author Bosco Garita Azofeifa 17/11/2013
     * @param t JTable tabla cuyo tamaño será cambiado
     * @param cantidad int número de columnas o filas a agregar o restar
     * @param tipo String "Columnas", "Filas" tipo de dato a modificar
     */
    public static void resizeTable(JTable t, int cantidad, String tipo) {
        DefaultTableModel dtm = (DefaultTableModel) t.getModel();
        if (tipo.equalsIgnoreCase("Filas")) {
            dtm.setRowCount(dtm.getRowCount() + cantidad);
        } else if (tipo.equalsIgnoreCase("Columnas")) {
            dtm.setColumnCount(dtm.getColumnCount() + cantidad);
        } // end if-else
        t.setModel(dtm);
    } // end resizeTable

    /**
     * Este método devuelve el precio basándose en el porcentaje de utilidad y el costo.
     * El porcentaje de utilidad lo toma del segundo parámetro que recibe y el costo del
     * primero.
     *
     * @author Bosco Garita Azofeifa
     * @param costo double costo base para el cálculo
     * @param utilidad double porcentaje de utilidad
     * @param conn Connection Conexión de base de datos a utilizar
     * @return double Precio
     * @throws java.sql.SQLException
     */
    public static double getPrecio(double costo, double utilidad, Connection conn) throws SQLException {
        double precio;

        precio = costo * utilidad / 100 + costo;

        if (UtilBD.redondearPrecios(conn)) {
            if (precio > (int) precio) {
                precio = Ut.redondear(precio, 0, 1);
            } // end if
        } // end if

        if (UtilBD.redondearPrecios5(conn)) {
            String temp = Ut.redondearA5(precio + "");
            precio = Double.parseDouble(temp);
        } // end if

        return precio;
    } // end getPrecio

    /**
     * Devuelve el tipo de dato para un campo de un ResultSet
     *
     * @param rs ResultSet RS que contiene el campo a analizar
     * @param col int Columna a analizar
     * @return String S=String, N=Numeric (cualquier número de punto flotante), B=Boolean,
     * L=Long (cualquier número entero), O=Object, D=Date, T=Timestamp, U=Undefined o NULL
     * @throws SQLException
     */
    public static String getFieldType(ResultSet rs, int col) throws SQLException {
        String fieldType;
        // Si el número de columna no existe retorno "U"
        if (rs.getMetaData().getColumnCount() < col) {
            return "U";
        } // end if
        switch (rs.getMetaData().getColumnType(col)) {
            case java.sql.Types.BIGINT:
                fieldType = "L";
                break;
            case java.sql.Types.BIT:
                fieldType = "L";
                break;
            case java.sql.Types.INTEGER:
                fieldType = "L";
                break;
            case java.sql.Types.TINYINT:
                fieldType = "L";
                break;
            case java.sql.Types.NUMERIC:
                fieldType = "N";
                break;
            case java.sql.Types.DECIMAL:
                fieldType = "N";
                break;
            case java.sql.Types.FLOAT:
                fieldType = "N";
                break;
            case java.sql.Types.DOUBLE:
                fieldType = "N";
                break;
            case java.sql.Types.BOOLEAN:
                fieldType = "B";
                break;
            case java.sql.Types.CHAR:
                fieldType = "S";
                break;
            case java.sql.Types.VARCHAR:
                fieldType = "S";
                break;
            case java.sql.Types.LONGNVARCHAR:
                fieldType = "S";
                break;
            case java.sql.Types.NCHAR:
                fieldType = "S";
                break;
            case java.sql.Types.NVARCHAR:
                fieldType = "S";
                break;
            case java.sql.Types.NULL:
                fieldType = "U";
                break;
            case java.sql.Types.DATE:
                fieldType = "D";
                break;
            case java.sql.Types.TIMESTAMP:
                fieldType = "T";
                break;
            default:
                fieldType = "O";
        } // end switch
        return fieldType;
    } // end

    /**
     * @author Bosco Garita Azofeifa 07/06/2014 Este método ordena una JTable por
     * cualquier columna. Nota: el valor de la primera columna no puede venir nulo. Si así
     * fuera toda la fila sería omitida.
     * @param t JTable tabla a ordenar
     * @param colN int número de columna por la que será ordenada la tabla
     */
    public static void sortTable(JTable t, int colN) {
        JTable table = new JTable();

        int rows = t.getModel().getRowCount();
        int cols = t.getModel().getColumnCount();
        int updateRow = 0;
        int dataRows;

        table.setAutoCreateRowSorter(true);
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.setColumnCount(cols);
        dtm.setRowCount(rows);
        table.setModel(dtm);

        // Paso los datos de la tabla original
        for (int row = 0; row < rows; row++) {
            // Si la primera celda viene nula se omite toda la fila.
            if (t.getValueAt(row, 0) == null) {
                continue;
            } // end if

            for (int col = 0; col < cols; col++) {
                if (t.getValueAt(row, col) != null) {
                    table.setValueAt(t.getValueAt(row, col), updateRow, col);
                } // end if
            } // end for
            updateRow++;
        } // end for

        // Cuento las filas con datos para luego establecer el número de filas
        // en el modelo.
        dataRows = Ut.countNotNull(table, 0);
        dtm.setRowCount(dataRows);

        // Ordeno los datos
        table.getRowSorter().toggleSortOrder(colN);

        // Pongo todos los valores de la tabla original en null para evitar que 
        // se duplique algún dato.
        for (int row = 0; row < t.getModel().getRowCount(); row++) {
            for (int col = 0; col < cols; col++) {
                t.setValueAt(null, row, col);
            } // end for
        } // end for

        // Vuelvo a trasladar los datos a la tabla original
        for (int row = 0; row < table.getModel().getRowCount(); row++) {
            for (int col = 0; col < cols; col++) {
                if (table.getValueAt(row, col) != null) {
                    t.setValueAt(table.getValueAt(row, col), row, col);
                } // end if
            } // end for
        } // end for
    } // end sortTable

    /**
     * Este método totaliza los datos de una JTable basándose en agrupaciones de la misma
     * forma que lo hace SQL (Select col1, col2, sum(coln) from tabla group by col1,
     * col2). IMPORTANTE: no se agrupan columnas con valor null.
     *
     * @author Bosco Garita Azofeifa 11/11/2014
     * @param t JTable que contiene los datos
     * @param groupBy int[] arreglo que contiene los números de columna que se usarán en
     * el group by
     * @param sumColumn int número de columna que será totalizada
     * @return String[][] arreglo (matriz) con el resultado de la suma por agrupación.
     */
    @SuppressWarnings("unchecked")
    public static String[][] totalTable(JTable t, int[] groupBy, int sumColumn) {
        int rows = 0;   // Filas que tendrá el arreglo a retornar
        List<String> l; // Aquí se agregan los datos separados por punto y coma
        l = new ArrayList<>();
        String[] groupValues = new String[groupBy.length]; // Variable para agrupar
        double value;       // Variable que se usará en la suma.
        String temp = "";   // Variable de trabajo

        // Recorrer la tabla
        for (int i = 0; i < t.getModel().getRowCount(); i++) {
            // Cargar el primer valor válido
            // Si la primer columna de del grupo está nula me salto el registro
            if (t.getValueAt(i, groupBy[0]) == null) {
                continue;
            } // end if

            // Cargar los valores de agrupación
            for (int j = 0; j < groupBy.length; j++) {
                groupValues[j] = t.getValueAt(i, groupBy[j]).toString();
            } // end for

            // Cargar el primer valor de la agrupación
            value = Double.parseDouble(t.getValueAt(i, sumColumn).toString());
            rows++;

            // Poner el valor como nulo para que no se repita
            t.setValueAt(null, i, groupBy[0]);
            // Fin cargar el primer valor válido

            // Ahora recorro el resto de la tabla acumulando los valores
            for (int k = i + 1; k < t.getModel().getRowCount(); k++) {
                // Si está nulo me lo brinco
                if (t.getValueAt(k, groupBy[0]) == null) {
                    continue;
                } // end if

                // Verificar la agrupación.
                boolean esIgual = true;
                for (int j = 0; j < groupBy.length; j++) {
                    // Con un solo campo que sea distinto ya no se debe acumular
                    if (!groupValues[j].equals(t.getValueAt(k, groupBy[j]))) {
                        esIgual = false;
                        break;
                    } // end if

                    esIgual = true;
                } // end for (int j = 0; j < columns.length; j++)

                // Si los valores de la agrupación son iguales entonces
                // acumulo el valor correspondiente.
                if (esIgual) {
                    value += Double.parseDouble(t.getValueAt(k, sumColumn).toString());
                    t.setValueAt(null, k, groupBy[0]);
                } // end if
            } // end for (int k = i+1; k < t.getModel().getRowCount(); k++)

            // Al salir de este for ya se tiene el total. Hay que guardarlo en
            // el list para continuar con la siguiente agrupación.
            for (String text : groupValues) {
                temp += text + ",";
            } // end for
            l.add(temp + value);
            temp = "";
            //value = 0.00;
        } // end for principal (recorrido de la JTable)

        // Crear el arreglo
        String[][] totales = new String[rows][groupBy.length + 1];
        String[] x;

        // Cargar el arreglo
        for (int j = 0; j < l.size(); j++) {
            temp = l.get(j);
            x = temp.split(",");
            System.arraycopy(x, 0, totales[j], 0, x.length);
        } // end for

        // Devolver el arreglo
        return totales;
    } // end totalTable

    /**
     * Este método devuelve el día de hoy en español para una fecha dada <br/>
     * Ejemplo: Lunes
     *
     * @param d date fecha desde donde se obtendrá el día
     * @return String hoy
     */
    public static String hoy(Date d) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(d);
        String day = "";
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                day = "Domingo";
                break;
            case 2:
                day = "Lunes";
                break;
            case 3:
                day = "Martes";
                break;
            case 4:
                day = "Miércoles";
                break;
            case 5:
                day = "Jueves";
                break;
            case 6:
                day = "Viernes";
                break;
            case 7:
                day = "Sábado";
                break;
        } // end switch

        return day;
    } // end hoy

    /**
     * Determina la parte numérica de un String que normalmente se obtiene de la
     * concatenación del código numérico, un guión o cualquier otro separador más el texto
     * o descripción. Ejemplo: "25-Caja auxiliar" el código númerico es el 25.
     *
     * @param text String contiene el texto a analizar
     * @param separator String puede ser cualquier caracter pero por lo general se usa un
     * guión para separar la parte numérica del resto del texto.
     * @return int código numérico
     */
    public static int getNumericCode(String text, String separator) {
        int posSep;     // Posición del separador

        posSep = Ut.getPosicion(text, "-");
        text = text.substring(0, posSep);
        return Integer.parseInt(text);
    } // end getNumericCode

    public static boolean isURLActive(String url) {
        boolean isActive = false;
        HttpURLConnection connection = null;
        long inicio = System.currentTimeMillis();
        int code = -1;

        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");

            code = connection.getResponseCode();
            long fin = System.currentTimeMillis();
            System.out.println("Codigo: " + code + " t:" + (fin - inicio));
        } catch (MalformedURLException e) {
            System.out.println("Error de URL: " + e);
        } catch (IOException e) {
            System.out.println("Error de conexion: " + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        if (code > 0) {
            isActive = true;
        }
        return isActive;
    } // end isURLActive

    public static boolean jPing(String ip) {
        boolean responde = false;
        InetAddress ping;
        try {
            ping = InetAddress.getByName(ip);
            if (ping.isReachable(5000)) {
                System.out.println(ip + " - responde!");
                responde = true;
            } else {
                System.out.println(ip + " - no responde!");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

        return responde;
    } // end jPing

    /**
     * Buscar un valor en una tabla y traer como resultado el valor que se encuentre en la
     * columna n. Si la tabla está vacía el resultado será #E/T (Empty Table) Si la
     * columna no existe el resultado será #C/M (Column Missing) Si el valor no existe el
     * resultado será #N/E (Not Exists)
     *
     * @author Bosco Garita Azofeifa 07/02/2016
     * @param text String texto a buscar
     * @param t JTable tabla en donde se realizará la búsqueda
     * @param col int Columna que tiene el valor esperado
     * @return String Resultado de la búsqueda
     */
    public static String vLookup(String text, JTable t, int col) {

        String result = "";
        boolean goNext;
        Object value;

        // Determinar los dos primeros posibles errores
        goNext = t.getModel().getRowCount() > 0;

        if (!goNext) {
            result = "#E/T";
        }

        if (goNext) {
            goNext = col >= 0 && col < t.getModel().getColumnCount();
            if (!goNext) {
                result = "#C/M";
            }
        }

        // Determinar el posible tercer error o el resultado si existe
        if (goNext) {
            result = "#N/E";
            for (int i = 0; i < t.getModel().getRowCount(); i++) {
                value = t.getValueAt(i, 0);
                if (value == null) {
                    continue;
                } // end if
                if (value.equals(text)) {
                    value = t.getValueAt(i, col);
                    if (value != null) {
                        result = value.toString();
                    }

                    break;
                } // end if (value.equals(text))
            } // end for
        } // end if
        return result;
    } // end vLookup

    /**
     * Busca un sub grupo de filas que contengan el texto a buscar y los coloca en otra
     * tabla para que se muestren al usuario.
     *
     * @author Bosco Garita, Abril 2016
     * @param origen JTable tabla que tiene el conjunto completo de los datos
     * @param destino JTable tabla que tendrá el sub conjunto encontrado
     * @param text String texto a buscar. Si viene un asterisco (*) se mostrarán todos los
     * valores.
     * @return true el texto exite, false no existe
     */
    public static boolean seekLike(JTable origen, JTable destino, String text) {
        boolean encontrado = false;
        clearJTable(destino);
        text = text.toUpperCase();
        String celValue;

        if (text.equals("*")) {
            clonarTabla(origen, destino);
            return true;
        } // end if

        for (int row = 0; row < origen.getModel().getRowCount(); row++) {
            for (int col = 0; col < origen.getModel().getColumnCount(); col++) {

                celValue = origen.getValueAt(row, col).toString().toUpperCase();
                // Si el texto está contenido en alguna de las celdas busco en
                // la tabla destino la primera fila que tenga el valor null en
                // la primera columna y le paso los valores de la fila origen.
                if (celValue.contains(text)) {
                    int nullRow = 0;
                    for (int i = 0; i < destino.getModel().getRowCount(); i++) {
                        if (destino.getValueAt(i, 0) == null) {
                            nullRow = i;
                            break;
                        } // end for
                    } // end for

                    // Agregar los valores a la tabla destino en la primera fila
                    // que tenga null
                    for (int i = 0; i < origen.getModel().getColumnCount(); i++) {
                        destino.setValueAt(origen.getValueAt(row, i), nullRow, i);
                    } // end for

                    // Como el valor ya fue localizado no debo continuar la búsqueda
                    // porque podría duplicarse el registro.
                    break;
                } // end if

            } // end for (Recorrido por las columnas de la tabla origen)

        } // end for (Recorrido por las filas de la tabla origen)

        return encontrado;
    } // end seekLike

    /**
     * Este método clona solo los datos por lo que se requiere que ambas tablas tenga la
     * misma estructura y tamaño.
     *
     * @author Bosco Garita, Abril 2016
     * @param origen JTable
     * @param destino JTable
     */
    private static void clonarTabla(JTable origen, JTable destino) {
        for (int row = 0; row < origen.getModel().getRowCount(); row++) {
            for (int col = 0; col < origen.getModel().getColumnCount(); col++) {
                destino.setValueAt(origen.getValueAt(row, col), row, col);
            } // end for
        } // end for
    } // end clonarTabla

    /**
     * @author Bosco Garita, 03/09/2016 Devuel el nombre del archivo sin extensión. NO
     * verifica si el archivo existe o no.
     * @param f File archivo a analizar
     * @return String nombre del archivo
     */
    public static String justName(File f) {
        String name = f.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(0, pos);
        } // end if
        return name;
    } // end justName

    /**
     * @author Bosco Garita, 03/09/2016 Devuel la extensión del archivo. NO verifica si el
     * archivo existe o no.
     * @param f File archivo a analizar
     * @return String extensión del archivo
     */
    public static String justExt(File f) {
        String fileName = f.getName();
        int pos = fileName.lastIndexOf(".");
        String ext = "";
        if (pos > 0) {
            ext = fileName.substring(pos + 1);
        } // end if
        return ext;
    } // end justExt

    /**
     * @author: Bosco Garita 15/07/2018 Este método convierte una fecha y hora a String y
     * retorna solamente la hora.
     * @return String con el formato "hh:mm:ss AM/PM"
     */
    public static String getCurrentTime() {
        Date d = new Date();
        //SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        SimpleDateFormat f = new SimpleDateFormat("hh:mm:ss a");
        return f.format(d);
    } // end getCurrentTime

    public static String mysqlVersion(Connection conn) throws SQLException {
        String version = "";
        String sqlSent = "show variables like '%version'";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            rs.beforeFirst();
            while (rs.next()) {
                if (rs.getString("variable_name").trim().equals("version")) {
                    version = rs.getString("Value");
                    break;
                } // end if
            } // end while
        } // end if
        ps.close();
        return version;
    } // end mysqlVersion

    /**
     * Convierte un archivo de texto a una cadena.
     *
     * @author Bosco Garita, 10/12/2014
     * @param fileName String nombre calificado del archivo a convertir
     * @param formatHTML boolean true=Convertir caracteres especiales a código HTML,
     * false=No hacerlo
     * @return String cadena con el contenido del archivo formateado con caracteres HTML.
     * @throws java.lang.Exception
     */
    public static String fileToString(String fileName, boolean formatHTML) throws Exception {
        String texto = "";
        String linea;

        // El Charset "UTF-8" funciona muy bien con las tildes y las eñes (por si acaso).
        BufferedReader br
                = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "ISO-8859-1"));
        while ((linea = br.readLine()) != null) {
            texto += linea;
        } // end while
        br.close();

        return formatHTML ? stringToHTML(texto) : texto;
    } // end fileToString

    public static String fileToString(Path path) {
        StringBuilder sb = new StringBuilder();
        try {
            if (path.toFile().exists()) {
                // Primero intento leer el archivo con UTF-8 y si se da un error
                // lo intento con ISO-8859-1
                Object[] content;
                try {
                    Stream<String> stream = Files.lines(path, Charset.forName("UTF-8"));
                    content = stream.toArray();
                } catch (Exception ex) {
                    Stream<String> stream = Files.lines(path, Charset.forName("ISO-8859-1"));
                    content = stream.toArray();
                } // end try-catch interno

                for (Object o : content) {
                    sb.append(o).append("\n");
                } // end for

            } else {
                sb.append("Archivo no encontrado.");
            } // end if-else
        } // end fileToString
        catch (Exception ex) {
            Logger.getLogger(Ut.class.getName()).log(Level.SEVERE, null, ex);
            sb.append(ex.getMessage());
        }
        return sb.toString();
    } // end fileToString

    /**
     * Prepara un string de texto con los códigos adecuados para los símbolos más comunes.
     *
     * @author Bosco Garita Azofeifa 08/02/2018
     * @param texto String texto que contiene los caracteres a convertir.
     * @return String texto del mensaje codificado para HTML
     */
    public static String stringToHTML(String texto) {
        String symbols[]
                = {"Á", "É", "Í", "Ó", "Ú",
                    "á", "é", "í", "ó", "ú",
                    "Ñ", "ñ", "®", "©",
                    "\"", "&"};
        String HTMLCode[]
                = {"&Aacute;", "&Eacute;", "&Iacute;", "&Oacute;", "&Uacute;",
                    "&aacute;", "&eacute;", "&iacute;", "&oacute;", "&uacute;",
                    "&Ntilde;", "&ntilde;", "&reg;", "&copy;",
                    "&quot;", "&amp;"};
        for (int i = 0; i < symbols.length; i++) {
            texto = texto.replace(symbols[i], HTMLCode[i]);
        } // end for
        return texto;
    } // end stringToHTML

    /**
     * Contar el número de veces que aparece un caracter en un String
     *
     * @author Bosco Garita 14/01/2015
     * @param texto String texto a evaluar
     * @param simbolo String caracter a contar
     * @return int número de veces que aparece
     */
    public static int contar(String texto, String simbolo) {
        int veces = 0;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.substring(i, i + 1).equals(simbolo)) {
                veces++;
            } // end if
        } // end for
        return veces;
    } // end if

    /**
     * Este método analiza el caracter recibido y dice si es vocal o no.
     *
     * @author Bosco Garita 29/04/2015, CR11343
     * @param c char Letra a revisar
     * @return true=Es vocal, false=No es vocal
     */
    public static boolean esVocal(char c) {
        return (Character.toLowerCase(c) == 'a')
                || (Character.toLowerCase(c) == 'e')
                || (Character.toLowerCase(c) == 'i')
                || (Character.toLowerCase(c) == 'o')
                || (Character.toLowerCase(c) == 'u');
    } // esVocal

    /**
     * Este método analiza el caracter recibido y dice si está en mayúscula o no.
     *
     * @author Bosco Garita 29/04/2015
     * @param c char Letra a revisar
     * @return true=Es mayúscula, false=No es mayúscula
     */
    public static boolean esMayuscula(char c) {
        if (!Character.isAlphabetic(c)) {
            return false;
        } // end if

        return (Character.toUpperCase(c) == c);
    } // esMayuscula

    /**
     * Este método analiza el texto recibido y dice cuántas mayúsculas vienen.
     *
     * @author Bosco Garita 29/04/2015
     * @param frase String texto a analizar
     * @return cuenta int cantidad de mayúsculas encontradas
     */
    public static int contarMayusculas(String frase) {
        if (frase == null || frase.trim().isEmpty()) {
            return 0;
        } // end if
        char[] xfrase = frase.toCharArray();
        int cuenta = 0;

        for (char c : xfrase) {
            cuenta += (esMayuscula(c) ? 1 : 0);
        } // end for
        return cuenta;
    } // esMayuscula

    /**
     * Calcular la edad de una persona.
     *
     * @author Bosco Garita, 14/02/2018
     * @param dateOfB String fecha de nacimiento
     * @return int[3] Años, meses, días.
     */
    public static int[] getAge(String dateOfB) {
        int edadx[] = new int[3];
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaNac = LocalDate.parse(dateOfB, fmt);
        LocalDate ahora = LocalDate.now();

        Period periodo = Period.between(fechaNac, ahora);

        edadx[0] = periodo.getYears();
        edadx[1] = periodo.getMonths();
        edadx[2] = periodo.getDays();

        return edadx;
    } // end getAge

    public static Properties getMailConfig() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        String configFile = "mail.props"; // Debe estar en la carpeta de instalacion del sistema
        File mailConfig = new File(configFile);

        // Establecer los parámetros predeterminados
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.user", "osais3112@gmail.com");
        props.put("mail.smtp.clave", "Bendicion3112$");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");

        if (mailConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(mailConfig)) {
                props.load(fis);
                fis.close();
            }
        } // end if
        return props;
    } // end getMailConfig

    /**
     * Retorna un nombre único ideal para nombres de archivo (type=1), nombres de
     * transacción, nombres de sesión web (type=2).
     *
     * @param type int 1=Nombre único ideal para archivos, 2=Nombre único universal (128
     * bits)
     * @return String unique name
     */
    public static String getUniqueName(int type) {
        String uniqueName;
        if (type == 1) {
            String fecha = Ut.dtoc(new Date());
            uniqueName = fecha.replaceAll("/", "-") + " " + Ut.getCurrentTime().replaceAll(":", " ");
        } else {
            uniqueName = UUID.randomUUID().toString();
        }

        return uniqueName;
    } // end uniqueName

    /**
     * Obtener parte de una fecha (día, mes o año) todo depende del parámetro recibido.
     *
     * @param date Date fecha de la cual se extraerá el valor solicitado.
     * @param part int parte de la fecha que se extraerá.
     * @return int parte de la fecha solicitado (los meses empiezan en cero). Si el
     * parámetro part no coincide con los valores de Ut.java para día, mes o año el valor
     * de retorno será un cero.
     */
    public static int getDatePart(Date date, int part) {
        int datePart = 0;
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        switch (part) {
            case Ut.DIA: {
                datePart = cal.get(Calendar.DAY_OF_MONTH);
                break;
            }
            case Ut.MES: {
                datePart = cal.get(Calendar.MONTH);
                break;
            }
            case Ut.AÑO: {
                datePart = cal.get(Calendar.YEAR);
                break;
            }
        } // end switch

        return datePart;
    } // end getDatePart

    public static void setRowNull(JTable table, int row) {
        if (row < 0) {
            return;
        } // end if

        for (int col = 0; col < table.getColumnModel().getColumnCount(); col++) {
            table.setValueAt(null, row, col);
        } // end for
    } // end setRowNull

    public static boolean isModuleAvailable(String module, Path path) {
        String modules = "";
        try {
            modules = Encripcion.decrypt(Ut.fileToString(path));
        } catch (Exception ex) {
            Logger.getLogger(Ut.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modules.contains(module);
    } // end isModuleAvailable
} // end utlitarios
