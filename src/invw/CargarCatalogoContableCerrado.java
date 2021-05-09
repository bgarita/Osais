package invw;

import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import logica.contabilidad.Cuenta;

/**
 * Esta clase carga el catálogo contable del sistema CG en fox para cada mes cerrado.
 * Correr (en VFP) el programa CatalogoC.prg que está en la carpeta C:\VCONTA\Migration
 * También hay una copia en este proyecto, en el paquete invw
 * 
 * Después de haber cargado el catálogo cerrado se debe ejecutar el siguiente SP
 * en base de datos: Call calcularNivelDeCuenta();
 *
 * @author Bosco Garita 30/04/2021
 */
public class CargarCatalogoContableCerrado {

    private final String tablaFox;
    private final String tablaMysql = "hcocatalogo";
    private final Cuenta cuenta;

    private final Connection conn;

    public CargarCatalogoContableCerrado(Connection c, String tablaFox)
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        this.conn = c;
        this.tablaFox = tablaFox;
        this.cuenta = new Cuenta(c);
        cargar();
    } // end constructor

    private void cargar()
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        DBFReader dbfReader = new DBFReader(tablaFox);
        Object registro[];
        PreparedStatement pr;
        String sqlUpdate;
        boolean existe;
        Date fecha_cierre;
        cuenta.setTabla(tablaMysql);

        while (dbfReader.hasNextRecord()) {
            registro = dbfReader.nextRecord();
            fecha_cierre = (java.util.Date) registro[25];
            cuenta.setMayor(registro[1].toString());
            cuenta.setSub_cta(registro[2].toString());
            cuenta.setSub_sub(registro[3].toString());
            cuenta.setColect(registro[4].toString());
            cuenta.setFecha_cierre(new java.sql.Timestamp(fecha_cierre.getTime()));
            cuenta.cargarRegistro(conn);

            // Degug:
            System.out.println(cuenta.getCuentaString());
            if (cuenta.getCuentaString().equals("110005002052")){
                System.out.println("Check values");
            }
            
            existe = !cuenta.getNom_cta().isEmpty();

            cuenta.setNom_cta(convertOldChar(registro[5].toString()));
            cuenta.setNivel(Short.parseShort(registro[6].toString()));
            cuenta.setTipo_cta(Short.parseShort(registro[7].toString()));
            
            //System.out.println("Cuenta a guardar: " + cuenta.getMayor() + "-" + cuenta.getSub_cta() + "-" + cuenta.getSub_sub() + "-" + cuenta.getColect() + " Fecha: " + fecha_cierre);

            if (existe) {
                sqlUpdate
                        = "Update " + tablaMysql + " set "
                        + "   nom_cta   = ?, "
                        + "   nivel     = ?, "
                        + "   tipo_cta  = ?, "
                        + "   ano_anter = ?, "
                        + "   db_fecha  = ?, "
                        + "   cr_fecha  = ?, "
                        + "   db_mes    = ?, "
                        + "   cr_mes    = ?, "
                        + "   fecha_cierre = ? "
                        + "Where mayor = ? and sub_cta = ? and sub_sub = ? and colect  = ?";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setString(1, cuenta.getNom_cta());
                pr.setShort(2, cuenta.getNivel());
                pr.setShort(3, cuenta.getTipo_cta());
                
                pr.setDouble(4, Double.parseDouble(registro[9].toString()));
                pr.setDouble(5, Double.parseDouble(registro[10].toString()));
                pr.setDouble(6, Double.parseDouble(registro[11].toString()));
                pr.setDouble(7, Double.parseDouble(registro[12].toString()));
                pr.setDouble(8, Double.parseDouble(registro[13].toString()));
                
                pr.setString(9, cuenta.getMayor());
                pr.setString(10, cuenta.getSub_cta());
                pr.setString(11, cuenta.getSub_sub());
                pr.setString(12, cuenta.getColect());
                pr.setTimestamp(13, new java.sql.Timestamp(fecha_cierre.getTime()));
                
            } else {
                sqlUpdate
                        = "Insert into " + tablaMysql
                        + " ("
                        + " mayor, "
                        + " sub_cta, "
                        + " sub_sub, "
                        + " colect, "
                        + " nom_cta, "
                        + " nivel, "
                        + " tipo_cta, "
                        + " ano_anter, "
                        + " db_fecha, "
                        + " cr_fecha, "
                        + " db_mes  , "
                        + " cr_mes,   "
                        + " fecha_cierre "
                        + ") "
                        + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setString(1, cuenta.getMayor());
                pr.setString(2, cuenta.getSub_cta());
                pr.setString(3, cuenta.getSub_sub());
                pr.setString(4, cuenta.getColect());
                pr.setString(5, cuenta.getNom_cta());
                pr.setShort(6, cuenta.getNivel());
                pr.setShort(7, cuenta.getTipo_cta());
                pr.setDouble(8, Double.parseDouble(registro[9].toString()));
                pr.setDouble(9, Double.parseDouble(registro[10].toString()));
                pr.setDouble(10, Double.parseDouble(registro[11].toString()));
                pr.setDouble(11, Double.parseDouble(registro[12].toString()));
                pr.setDouble(12, Double.parseDouble(registro[13].toString()));
                pr.setTimestamp(13, new java.sql.Timestamp(fecha_cierre.getTime()));
            }
            pr.executeUpdate();
        } // end while
        dbfReader.close();
    } // end main

    private String convertOldChar(String text) {
        if (text.contains("�")){
            text = text.replace("�", "í");
        }
        return text;
    }

} // end LeerDBFs
