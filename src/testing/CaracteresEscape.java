/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

/**
 *
 * @author Bosco
 */
public class CaracteresEscape {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String a = "Daniel O'Hara";
        System.out.println(a);
        a = a.replace("'", "\\'");
        System.out.println(a);
    }

}
