/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import interfase.transacciones.RegistroFacturasV;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JApplet;

/**
 *
 * @author bosco
 */
public class Sonidos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File f = new File("beep-06.wav");
        System.out.println(f.getAbsolutePath());
        
        URI u = f.toURI();
        try {
            URL u2 = u.toURL();
            AudioClip sonido=JApplet.newAudioClip(u2);
            
            sonido.play();
            Thread.sleep(100);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RegistroFacturasV.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sonidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
