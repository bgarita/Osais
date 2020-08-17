/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.File;
import logica.utilitarios.Archivos;

/**
 *
 * @author bosco
 */
public class TestFileCompare {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Archivos a = new Archivos();
        File file1, file2;
        file1 = new File("/home/bosco/osais.txt");
        file2 = new File("/home/bosco/osais (copia).txt");
        a.compareFiles(file1, file2);
        
    }
    
}
