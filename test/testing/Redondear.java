/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
public class Redondear {
    public static void main(String[] args) {
        double valor = 125.324545;
        String val = valor+"";
        BigDecimal big = new BigDecimal(val);
        big = big.setScale(5, RoundingMode.HALF_EVEN);
        System.out.println("Número : "+big);
        
        System.out.println("Número (arriba) : " +Ut.redondear(valor, 5, 1));
        
    }
}
