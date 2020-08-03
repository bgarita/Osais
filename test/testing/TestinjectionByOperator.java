/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class TestinjectionByOperator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Ut.injectionByOperator(
                "Select * from inarticu where or 1 = 0 and or 2=2 and 3=A".toUpperCase());
    }
}
