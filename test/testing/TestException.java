/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import Exceptions.OsaisException;

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
            throw new OsaisException(
                "El valor a consultar [?????] " +
                " no es único.");
        } catch (OsaisException ex){
            System.out.println(ex.getMessage());
        }
    }
}
