
package testing;

import java.awt.Color;


/**
 *
 * @author Bosco
 */
public class TestConsolaIntegridad {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        ConsolaIntegridad ci = new ConsolaIntegridad();
        
        ci.setVisible(true);
        ci.agregarTexto("Prueba uno..", 1, Color.BLUE);
        Thread.sleep(5000);
        ci.agregarTexto(" Final uno", 1, Color.magenta);
        ci.agregarTexto("Prueba dos..", 2, Color.BLUE);
        Thread.sleep(5000);
        ci.agregarTexto(" Final dos", 2, Color.magenta);
        Thread.sleep(3000);
        
        Thread.sleep(2000);
        ci.setVisible(false);
        ci.dispose();
        ci = null;
        System.out.println("Terminó");
    }

}
