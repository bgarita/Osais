/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

/**
 *
 * @author bosco
 */
public class SortArray {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[][] array;
        array = new String[3][2];
        array[0][0] = "Bosco";
        array[0][1] = "20";
        array[1][0] = "Marvin";
        array[1][1] = "30";
        array[2][0] = "Eduardo";
        array[2][1] = "50";
        
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < 2; j++){
                System.out.print(array[i][j]+ " ") ;
            }
            System.out.println();
        }
        
    }
}
