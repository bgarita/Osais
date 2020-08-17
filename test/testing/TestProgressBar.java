/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import logica.utilitarios.ProcessProgressBar;

/**
 *
 * @author bosco
 */
public class TestProgressBar {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ProcessProgressBar pp = new ProcessProgressBar(null, "Cierre mensual");
            pp.setAno(2014);
            pp.setMes(10);
            pp.setMaximumValue(12);
            //pp.setCs(cs);
            pp.setVisible(true);
            pp.start();
    }
    
}
