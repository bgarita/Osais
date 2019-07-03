/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import interfase.reportes.ReportesProgressBar;
import java.sql.Connection;

/**
 *
 * @author bosco
 */
public class TestPB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection c = null;
        ReportesProgressBar pb = 
                new ReportesProgressBar(c, "Título", "Formulario", "Query", "Filtro");
        pb.setBorderTitle("Guardando registros..");
        pb.setVisible(true);
        pb.start();
    }
}
