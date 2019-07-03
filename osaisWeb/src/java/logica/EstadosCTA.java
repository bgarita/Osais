
package logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Bosco
 */
public class EstadosCTA {
    private Connection conn;
    private int clicode;
    private String periodos;
    private String orden;
    private String factCan;
    private ResultSet rs;
    private String[][] aDetalle;
    private String saldo;
    private String compras;
    private String pagos;
    private String clidesc;
    private String desde;

    public EstadosCTA(Connection c, int clicode, String periodos,
            String orden, String factCan) throws SQLException{
        this.conn     = c;
        this.clicode  = clicode;
        this.periodos = periodos;
        this.orden    = orden;
        this.factCan  = factCan;
        this.consultar();
    } // end constructor

    private void consultar() throws SQLException {
        double facmont = 0.00, monto = 0.00;
        String sqlSent =
                "Call Rep_EstadoCtaCXC(?," +
                    periodos + "," +
                    factCan  + "," +
                    orden    +
                    ")";

        if (conn.isClosed()){
            System.out.println("Conexión cerrada.");
            return;
        } // end if
        
        PreparedStatement ps = conn.prepareStatement(sqlSent);

        // Solo se parametriza este valor porque es el único que el usuario
        // puede digitar.  El resto son opciones que debe escoger y por lo tanto
        // solo pueden venir los valores previstos.
        ps.setInt(1, clicode);
        
        rs = ps.executeQuery();

        // Cargar el arreglo
        rs.last();
        aDetalle = new String[rs.getRow()][12];
        rs.beforeFirst();
        int row = 0;
        while (rs.next()){
            aDetalle[row][0] = rs.getString("CredCont"); // Crédito o contado
            aDetalle[row][1] = rs.getString("facnume");  // Factura/ND
            aDetalle[row][2] = rs.getString("facond");  // Factura o ND
            aDetalle[row][3] = Utilitarios.dtoc(rs.getDate("facfech"));
            aDetalle[row][4] = Utilitarios.dtoc(rs.getDate("facfepa"));
            aDetalle[row][5] = Utilitarios.Fdecimal(
                    rs.getString("facmont"), "#,##0.00");
            aDetalle[row][6] = Utilitarios.Fdecimal(
                    rs.getString("facsald"), "#,##0.00");
            aDetalle[row][7] = rs.getString("vencida");
            aDetalle[row][8] = rs.getString("recnume");  // Recibo/NC
            aDetalle[row][9] = rs.getString("reconc");  // Recibo o NC
            aDetalle[row][10] = Utilitarios.dtoc(rs.getDate("fecha"));
            aDetalle[row][11] = Utilitarios.Fdecimal(
                    rs.getString("monto"), "#,##0.00");

            if (rs.getString("facmont") != null){
                facmont += rs.getDouble("facmont");
            } // end if
            if (rs.getString("monto") != null){
                monto += rs.getDouble("monto");
            } // end if
            row++;
        } // end while

        compras = Utilitarios.Fdecimal(facmont + "", "#,##0.00");
        pagos   = Utilitarios.Fdecimal(monto + "", "#,##0.00");

        // Cargar el saldo
        rs.first();
        saldo   = Utilitarios.Fdecimal(rs.getString("saldo"), "#,##0.00");
        clidesc = rs.getString("clidesc");
        desde   = Utilitarios.dtoc(rs.getDate("Desde"));
    } // end consultar

    // Métodos getter
    public String[][] getDetalle(){
        return aDetalle;
    } // end getDetalle

    public String getSaldo(){
        return saldo;
    } // end getSaldo

    public String getCompras() {
        return compras;
    }

    public String getPagos() {
        return pagos;
    }

    public String getClidesc() {
        return clidesc;
    }

    public String getDesde() {
        return desde;
    }

} // end class
