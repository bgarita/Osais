
package logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Bosco Garita 29/08/2011 09:51 pm
 * Consultar los datos más relevantes de una factura.  Estos datos serán devueltos
 * mediantes los métodos públicos accesorios (getEncabezado, getDetalle y getPie)
 */
public class FacturasNDyNC {
    private Connection conn;
    private String facnume;
    private String tipoDoc;
    private ResultSet rs;
    private String[]   aEncabezado;
    private String[][] aDetalle;
    private String[]   aPie;

    public FacturasNDyNC(Connection c, String facnume, String tipoDoc) throws SQLException {
        this.conn    = c;
        this.facnume = facnume;
        this.tipoDoc = tipoDoc;
        aEncabezado  = new String[7];
        aPie         = new String[5];
        this.consultar();
    } // end constructor

    private void consultar() throws SQLException{
        String sqlSent =
                "Call ConsultarFactNDNC(" + facnume + "," + tipoDoc + ")";
        PreparedStatement ps = conn.prepareCall(sqlSent);
        rs = ps.executeQuery();

        if (rs == null || !rs.first()){
            return;
        } // end if

        // Cargar el encabezado
        aEncabezado[0] = rs.getString("facfech");
        aEncabezado[1] = rs.getString("user");
        aEncabezado[2] = rs.getString("tipo");
        aEncabezado[3] = rs.getString("facestado");
        aEncabezado[4] = rs.getString("simbolo") + " " + rs.getString("moneda");
        aEncabezado[5] = rs.getString("clidesc");
        aEncabezado[6] = rs.getString("vendedor");

        // Cargar el pie
        aPie[0] = Utilitarios.Fdecimal(rs.getString("facimve"),"#,##0.00");
        aPie[1] = Utilitarios.Fdecimal(rs.getString("facdesc"),"#,##0.00");
        aPie[2] = Utilitarios.Fdecimal(rs.getString("Total"),"#,##0.00");
        aPie[3] = Utilitarios.Fdecimal(rs.getString("facmonexp"),"#,##0.00");
        aPie[4] = Utilitarios.Fdecimal(rs.getString("facsald"),"#,##0.00");

        // Cargar el arreglo detalle
        rs.last();
        aDetalle = new String[rs.getRow()][6];
        rs.beforeFirst();
        int row = 0;
        while (rs.next()){
            aDetalle[row][0] = rs.getString("artcode");
            aDetalle[row][1] = rs.getString("bodega" );
            aDetalle[row][2] = rs.getString("artdesc");
            aDetalle[row][3] = Utilitarios.Fdecimal(rs.getString("faccant"),"#,##0.00");
            aDetalle[row][4] = Utilitarios.Fdecimal(rs.getString("artprec"),"#,##0.00");
            aDetalle[row][5] = Utilitarios.Fdecimal(rs.getString("facmont"),"#,##0.00");
            row++;
        } // end while
    } // end consultar
    
    public String[] getEncabezado(){
        return aEncabezado;
    } // end getEncabezado

    public String[] getPie(){
        return aPie;
    } // end getPie

    public String[][] getDetalle(){
        return aDetalle;
    } // end getPie
} // end class
