package testing;

import contabilidad.logica.Indent;

/**
 *
 * @author bosco
 */
public class TestIndent {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Indent i = new Indent();
        String cta = "activos fijos";
        System.out.println(i.getFormatted(cta, 1, false, 3));
    }
    
}
