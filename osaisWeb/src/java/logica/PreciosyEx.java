
package logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Bosco
 */
public class PreciosyEx {
    private Connection conn;
    private String artcode;
    private ResultSet rs;
    private String[][] aPrecios;
    private String[][] aExistencias;

    public PreciosyEx(Connection c, String artcode) throws SQLException{
        this.conn = c;
        this.artcode = artcode;
        this.consultar();
    } // end constructor

    private void consultar() throws SQLException{
        String sqlSent =
                "Select artdesc, artpre1, artpre2, artpre3, artpre4, artpre5 " +
                "From inarticu Where artcode = ?";
        if (artcode == null || artcode.equals("")){
            return;
        } // end if

        if (conn.isClosed()){
            System.out.println("Conexión cerrada.");
            return;
        }
        
        PreparedStatement ps = conn.prepareStatement(sqlSent);
        ps.setString(1, artcode);
        rs = ps.executeQuery();

        // Cargar el arreglo de precios
        rs.last();
        aPrecios = new String[rs.getRow()][6];
        rs.beforeFirst();
        int row = 0;
        while (rs.next()){
            aPrecios[row][0] = rs.getString("artdesc");
            aPrecios[row][1] = Utilitarios.Fdecimal(
                    rs.getString("artpre1").trim(), "#,##0.00");
            aPrecios[row][2] = Utilitarios.Fdecimal(
                    rs.getString("artpre2").trim(), "#,##0.00");
            aPrecios[row][3] = Utilitarios.Fdecimal(
                    rs.getString("artpre3").trim(), "#,##0.00");
            aPrecios[row][4] = Utilitarios.Fdecimal(
                    rs.getString("artpre4").trim(), "#,##0.00");
            aPrecios[row][5] = Utilitarios.Fdecimal(
                    rs.getString("artpre5").trim(), "#,##0.00");
            row++;
        } // end while

        // Procesar las existencias
        sqlSent =
                "Select b.descrip, a.artexis, a.artreserv, a.minimo " +
                "From bodexis A " +
                "Inner join bodegas B on a.bodega = b.bodega " +
                "Where a.artcode = ? order by 1";
        ps = conn.prepareCall(sqlSent);
        ps.setString(1, artcode);
        rs = ps.executeQuery();

        // Cargar el arreglo de existencias
        rs.last();
        aExistencias = new String[rs.getRow()][4];
        rs.beforeFirst();
        row = 0;

        while (rs.next()){
            aExistencias[row][0] = rs.getString("descrip");
            aExistencias[row][1] = Utilitarios.Fdecimal(
                    rs.getString("artexis").trim(), "#,##0.00");
            aExistencias[row][2] = Utilitarios.Fdecimal(
                    rs.getString("artreserv").trim(), "#,##0.00");
            aExistencias[row][3] = Utilitarios.Fdecimal(
                    rs.getString("minimo").trim(), "#,##0.00");
            row++;
        } // end while
    } // end consultar

    // Devolver el resultado de precios
    public String[][] getPrecios(){
        return aPrecios;
    } // end getPrecios

    // Devolver el resultado de existencias
    public String[][] getExistencias(){
        return aExistencias;
    } // end getExistencias
} // end class
