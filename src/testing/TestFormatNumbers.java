/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
public class TestFormatNumbers {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        double d = 1364570.34566;
        String s = String.format("%,.3f", d);
        System.out.println(s);
        // Estos dos métodos funcionan exactamente igual.  Solo hay dos diferencias
        // El segundo es más moderno pero el primero acepta otros formatos.
        System.out.println(Ut.fDecimal(d+"", "¢#,##0.000"));
        System.out.println(Ut.fDecimal(d+"", 3));

        int i = 123;
        // Esta forma solo fuciona para enteros y solo se puede rellenar con ceros.
        System.out.println(String.format("%05d",i));
        // Este método funciona para cualquier número convertido a string
        // y con cualquier caracter de relleno.
        System.out.println(Ut.lpad("123.5", "&", 7));
    }

}
