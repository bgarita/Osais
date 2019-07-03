/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import logica.Fondo;
import java.awt.Frame;
import javax.swing.JFrame;

/**
 *
 * @author Bosco Garita
 */
public class TestFondo extends JFrame {
    private Fondo f;

    public TestFondo(){
        f = new Fondo();
        this.add(f);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
    }
    public void setImagen(String imageIcon){
        f.setImagen(imageIcon);
    } // end setImagen

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestFondo testFondo = new TestFondo();
        testFondo.setImagen("C:\\Java Programs\\osais\\src\\Images\\DSC00789.jpg");
        testFondo.setVisible(true);        
    }

}
