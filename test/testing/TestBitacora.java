package testing;

import Mail.Bitacora;

/**
 *
 * @author bgari
 */
public class TestBitacora {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Bitacora b = new Bitacora();
        b.setLogLevel(Bitacora.INFO);
        b.writeToLog("TestBitacora" + "--> " + "Pruebas");
    }
    
}
