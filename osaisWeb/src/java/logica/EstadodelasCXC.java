
package logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 *
 * @author Bosco 18/10/2011
 */
public class EstadodelasCXC {
    private Connection conn;
    private Calendar cal;
    private int orden;
    private String[][] aDetalle;
    private double monto, saldo;
    
    public EstadodelasCXC(Connection c, Calendar cal, int orden) throws SQLException{
        this.conn = c;
        this.cal  = cal;
        this.orden = orden;
        consultar();
    } // end constructor


    private void consultar() throws SQLException {
        String query, facfech;

        facfech = Utilitarios.fechaHoraSQL(cal.getTime(), "23:59:59");

        query = "Call Rep_EstadoDeLasCXC(" +
                facfech + "," +
                orden   + ")";

        if (conn.isClosed()){
            System.out.println("Conexión cerrada.");
            return;
        }
        PreparedStatement pr = conn.prepareStatement(query);
        ResultSet rs = pr.executeQuery();

        // Cargar el arreglo
        rs.last();
        aDetalle = new String[rs.getRow()][7];
        rs.beforeFirst();
        int row = 0;
        monto = 0.00;
        saldo = 0.00;
        while (rs.next()){
            aDetalle[row][0] = rs.getString("clicode");  // Código
            aDetalle[row][1] = rs.getString("clidesc");  // Nombre
            aDetalle[row][2] = rs.getString("facnume");  // Documento
            aDetalle[row][3] = rs.getString("tipo");     // Tipo doc
            aDetalle[row][4] = Utilitarios.dtoc(rs.getDate("facfech"));
            aDetalle[row][5] = Utilitarios.Fdecimal(
                    rs.getString("facmont"), "#,##0.00"); // monto
            aDetalle[row][6] = Utilitarios.Fdecimal(
                    rs.getString("facsald"), "#,##0.00"); // Saldo

            // Totalizar los montos
            monto += rs.getDouble("facmont");
            saldo += rs.getDouble("facsald");
            row++;
        } // end while
        rs.close();
    } // end consultar

    /**
     * Total documentos
     * @return monto double
     */
    public double getMonto() {
        return monto;
    }

    /**
     * Total saldo
     * @return saldo double
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * Arreglo con todo el detalle de los documentos
     * @return aDetalle String[][]
     */
    public String[][] getDetalle() {
        return aDetalle;
    }
} // end class
