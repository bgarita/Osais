package invw;

import accesoDatos.CMD;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import logica.contabilidad.PeriodoContable;

/**
 * Esta clase carga los periodos contables del sistema CG en fox. Debe abrir el
 * archivo aslcgpe.dbf y ejecutar el siguiente comando en FOX: COPY TO C:\VCONTA\Migration\PER2.DBF TYPE FOX2X
 * para que esta clase pueda procesar la tabla. 
 *
 * @author Bosco Garita 30/08/2020
 */
public class CargarPeriodosContables {

    private final String tablaFox;
    private final String tablaMysql = "coperiodoco";
    private final PeriodoContable periodo;

    private final Connection conn;

    public CargarPeriodosContables(Connection c, String tablaFox)
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        this.conn = c;
        this.tablaFox = tablaFox;
        this.periodo = new PeriodoContable();
        cargar();
    } // end constructor

    private void cargar()
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        DBFReader d = new DBFReader(tablaFox);
        Object registro[];
        PreparedStatement ps;
        String sqlUpdate;
        boolean existe;
        java.sql.Date fecha_in;
        java.sql.Date fecha_fi;

        while (d.hasNextRecord()) {
            registro = d.nextRecord();
            periodo.setMes(Integer.parseInt(registro[0].toString()));
            periodo.setAño(Integer.parseInt(registro[1].toString()));
            
            periodo.cargarRegistro(conn); // Si el registro existe lo cargo
            
            System.out.println(periodo.getDescrip());
            
            existe = !periodo.getDescrip().isEmpty();
            
            periodo.setDescrip(registro[2].toString());
            periodo.setFecha_in((java.util.Date) registro[3]);
            periodo.setFecha_fi((java.util.Date) registro[4]);
            periodo.setCerrado(((boolean)registro[5] ? 1: 0));
            
            fecha_in = new java.sql.Date(periodo.getFecha_in().getTime());
            fecha_fi = new java.sql.Date(periodo.getFecha_fi().getTime());
            
            if (existe) {
                sqlUpdate
                        = "Update " + tablaMysql + " set "
                        + "   descrip = ?, "
                        + "   fecha_in = ?, "
                        + "   fecha_fi = ?, "
                        + "   cerrado = ? "
                        + "Where mes = ? and año = ?";
                ps = conn.prepareStatement(sqlUpdate);
                ps.setString(1, periodo.getDescrip());
                ps.setDate(2, fecha_in);
                ps.setDate(3, fecha_fi);
                ps.setInt(4, periodo.getCerrado());
                ps.setInt(5, periodo.getMes());
                ps.setInt(6, periodo.getAño());
            } else {
                sqlUpdate
                        = "Insert into " + tablaMysql
                        + " (mes, año, descrip, fecha_in, fecha_fi, cerrado) "
                        + "values(?,?,?,?,?,?)";
                
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, periodo.getMes());
                ps.setInt(2, periodo.getAño());
                ps.setString(3, periodo.getDescrip());
                ps.setDate(4, fecha_in);
                ps.setDate(5, fecha_fi);
                ps.setInt(6, periodo.getCerrado());
            }
            CMD.update(ps);
        } // end while
        d.close();
    } // end main

    private String convertOldChar(String text) {
        if (text.contains("�")){
            text = text.replace("�", "í");
        }
        return text;
    }

} // end LeerDBFs
