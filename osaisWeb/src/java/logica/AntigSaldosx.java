package logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Bosco 10/09/2011 04:17 pm
 */
public class AntigSaldosx {
    private Connection conn;
    private String desde, hasta; // rango de clientes
    private String orden;
    /*
     *  "1" Fecha de vencimiento
        "2" Código de cliente
        "3" Nombre del cliente
        "4" Número de factura
        "5" Saldo de la factura (descendente)
     */
    private String vencidas;        // 0=No, 1=Si
    private String saldosMay;       // Saldos mayores a
    private String fechaMostrar;    // 1=Fecha de vencimiento, 2=Fecha de emisión
    private String clasif1,clasif2,clasif3; // Clasificación de vencimientos
    private String[][] aDetalle;
    private double totalClasif1, totalClasif2, totalClasif3; // Totales

    public AntigSaldosx(Connection conn, String desde, String hasta,
            String orden, String vencidas, String saldosMay, String fecha,
            String clasif1, String clasif2, String clasif3) throws SQLException {
        this.conn = conn;
        this.desde = desde.isEmpty() ? "0":desde;
        this.hasta = hasta.isEmpty() ? "0":hasta;
        this.orden = orden;
        this.vencidas = vencidas;
        this.saldosMay = saldosMay;
        this.fechaMostrar = fecha;
        this.clasif1 = clasif1;
        this.clasif2 = clasif2;
        this.clasif3 = clasif3;
        totalClasif1 = 0;
        totalClasif2 = 0;
        totalClasif3 = 0;
        consultar();
    } // end constructor

    private void consultar() throws SQLException {
        String query = "Call Rep_AntigSaldCXC(" +
             desde + ","  + // Rango inicial de clientes (0=Primero)
             hasta + ","  + // Rango final de clientes   (0=Último)
             vencidas  + ","  + // ¿Solo facturas vencidas? (1=Si,0=No)
             saldosMay + ","  + // Saldos mayores a n
             clasif1   + ","  + // Primer grupo de clasificación  (normalmente de 0 a 30 días)
             clasif2   + ","  + // Segundo grupo de clasificación (normalmente de 31 a 60 días)
             clasif3   + ","  + // Tercer grupo de clasificación  (normalmente más de 60 días)
             fechaMostrar     + ","  + // 1=Mostrar fecha de vencimiento, 2=Mostrar fecha de emisión
             orden     + ")";   // Ordenamiento

        if (conn.isClosed()){
            System.out.println("Conexión cerrada.");
            return;
        }
        PreparedStatement pr = conn.prepareStatement(query);
        ResultSet rs = pr.executeQuery();

        // Cargar el arreglo
        rs.last();
        aDetalle = new String[rs.getRow()][11];
        rs.beforeFirst();
        int row = 0;
        while (rs.next()){
            aDetalle[row][0] = rs.getString("clicode");  // Código
            aDetalle[row][1] = rs.getString("clidesc");  // Nombre
            aDetalle[row][2] = rs.getString("clitel1");  // Teléfono
            aDetalle[row][3] = rs.getString("clicelu");  // Celular
            aDetalle[row][4] = rs.getString("facnume");  // Fact/ND
            aDetalle[row][5] = Utilitarios.dtoc(rs.getDate("facfech"));
            aDetalle[row][6] = Utilitarios.dtoc(rs.getDate("vence"));
            aDetalle[row][7] = rs.getString("DiasVenc");  // Días vencida
            aDetalle[row][8] = Utilitarios.Fdecimal(
                    rs.getString("Clasif1"), "#,##0.00");
            aDetalle[row][9] = Utilitarios.Fdecimal(
                    rs.getString("Clasif2"), "#,##0.00");
            aDetalle[row][10] = Utilitarios.Fdecimal(
                    rs.getString("Clasif3"), "#,##0.00");
            
            // Totalizar las clasificaciones
            if (rs.getString("Clasif1") != null){
                totalClasif1 += rs.getDouble("Clasif1");
            } // end if
            if (rs.getString("Clasif2") != null){
                totalClasif2 += rs.getDouble("Clasif2");
            } // end if
            if (rs.getString("Clasif3") != null){
                totalClasif3 += rs.getDouble("Clasif3");
            } // end if
            row++;
        } // end while
        rs.close();
    } // end consultar

    public String[][] getaDetalle() {
        return aDetalle;
    }

    public double getTotalClasif1() {
        return totalClasif1;
    }

    public double getTotalClasif2() {
        return totalClasif2;
    }

    public double getTotalClasif3() {
        return totalClasif3;
    }

} // end class
