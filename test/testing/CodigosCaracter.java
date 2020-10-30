/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import accesoDatos.UtilBD;

/**
 *
 * @author Bosco
 */
public class CodigosCaracter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String a = "Prednisolona, ​​combinaciones y preparaciones como corticosteroides para el tratamiento oral local";
        System.out.println(a.length());
        a = UtilBD.remove8203Char(a);
        System.out.println(a.length());
        char[] codigos = a.toCharArray();
        for (char c : codigos){
            System.out.print(c);
            System.out.print("=");
            System.out.println(c + 0);
        }
        
    }

}
