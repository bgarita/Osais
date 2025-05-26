/*
 * Esta clase solo tiene como objetivo retornar un string de 36
 * posiciones que contiene las cuentas de mayor a partir de una
 * cuenta de movimientos.
 */
package contabilidad.logica;

import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, 25/05/2025
 */
public class Mayores {

    public static final int LONGITUD_MAXIMA = 12; // Longitud máxima de la cuenta
    public static final int POSICION_PRIMER_NIVEL = 3;
    public static final int POSICION_SEGUNDO_NIVEL = 6;
    public static final int POSICION_TERCER_NIVEL = 9;

    public static String getMayores(String cuenta) {
        String cuentasMayores;
        int posCuenta; // Se usa para optener la posición de la cuenta

        // Creo todas las cuentas de mayor en un solo string. (36 posiciones)
        cuentasMayores = cuenta.substring(0, POSICION_PRIMER_NIVEL);

        // Cuenta de mayor primer nivel
        cuentasMayores = Ut.rpad(cuentasMayores, "0", LONGITUD_MAXIMA);

        // Cuenta de mayor segundo nivel
        String temp = cuenta.substring(0, POSICION_SEGUNDO_NIVEL);
        temp = Ut.rpad(temp, "0", LONGITUD_MAXIMA);
        cuentasMayores += temp;

        // Cuenta de mayor tercer nivel
        temp = cuenta.substring(0, POSICION_TERCER_NIVEL);
        temp = Ut.rpad(temp, "0", LONGITUD_MAXIMA);
        cuentasMayores += temp;

        // Obtener la posición de la cuenta dentro todo el String
        posCuenta = Ut.AT(cuentasMayores, cuenta); // Cambiar esto por cuentaMayor.indexOf() 25/05/2025
        if (posCuenta > 0) {
            cuentasMayores = cuentasMayores.substring(0, posCuenta);
        } // end if

        return cuentasMayores;
    }
}
