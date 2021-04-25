package testing;

import logica.utilitarios.Encripcion;

/**
 *
 * @author bgarita
 */
public class TestEncripcion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String laflor = "INV,CXP,FAC,COM,FE";
        System.out.println(Encripcion.encript(laflor));
        //System.out.println(Encripcion.decrypt("MCrdYLWhGHiDwK+1o59pnpFeUXvQ3uAlf+qg7xiTwbKt4ClgTSdDoiRMjT6q/pkC"));
    }
    
}
