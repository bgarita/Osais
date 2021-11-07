/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.nio.file.Path;
import java.nio.file.Paths;
import logica.utilitarios.Ut;

/**
 *
 * @author bgari
 */
public class TestFileToString {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String fileName = "C:\\Java Programs\\osais\\osais.txt";
        String texto = Ut.fileToString(fileName, false);
        System.out.println(texto);
        Path path = Paths.get(fileName);
        texto = Ut.fileToString(path);
        System.out.println(texto);
        
        String[] sArray = Ut.fileToArray(path);
        
        for (String line:sArray){
            System.out.println(line);
        }
        
    }
    
}
