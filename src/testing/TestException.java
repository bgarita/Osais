/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import Exceptions.NotUniqueValueException;

/**
 *
 * @author Bosco
 */
public class TestException {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            throw new NotUniqueValueException(
                "El valor a consultar [?????] " +
                " no es único.");
        } catch (NotUniqueValueException ex){
            System.out.println(ex.getMessage());
        }
    }
}
