/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class FixIP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String url = "192.168.1.10:3306/sai";
        int pos = url.lastIndexOf(".");
        int colon   = url.indexOf(":");
        System.out.println("Original: " + url);
        String sinceColon = url.substring(colon);
        
        int lastIPNumber = Integer.parseInt(url.substring(pos+1, colon));
        url = url.substring(0, pos+1) + (lastIPNumber + 1);
        System.out.println("Nuevo: " + url);
        
        // Test isURLActive
        System.out.println(Ut.isURLActive("http://www.repcod.com"));
        
        String ip = "192.168.1.10";
        // Test jPing
        System.out.println("Validando si la IP " + ip + " está activa...");
        System.out.println("Esta IP " + (Ut.jPing(ip)? "":"no ") + " está activa.");
    }
    
}
