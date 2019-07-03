package invw;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import logica.contabilidad.Cuenta;

/**
 * Esta clase carga el catálogo contable del sistema CG en fox
 * Debe abrir el archivo aslcg01.dbf y ejecutar el siguiente comando
 * en FOX: COPY TO Z:\home\bosco\CAT2.DBF TYPE FOX2X para que esta
 * clase pueda procesar la tabla.
 * Después de haber cargado el catálogo se debe ejecutar el siguiente SP
 * en base de datos: Call calcularNivelDeCuenta();
 * @author Bosco Garita 07/09/2013
 */
public class CargarCatalogoContable {
    private final String tablaFox;
    private final String tablaMysql = "cocatalogo";
    private final Cuenta cuenta;
    
    private final Connection conn;

    public CargarCatalogoContable(Connection c, String tablaFox)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException{
        this.conn = c;
        this.tablaFox = tablaFox;
        this.cuenta = new Cuenta(c);
        cargar();
    } // end constructor
    

    private void cargar()
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        DBFReader d = new DBFReader(tablaFox);
        Object registro[];
        PreparedStatement pr;
        String sqlUpdate;
        boolean existe;
        
        //CMD.transaction(conn, CMD.START_TRANSACTION);
        while (d.hasNextRecord()){
            registro = d.nextRecord();
            cuenta.setMayor(registro[1].toString());
            cuenta.setSub_cta(registro[2].toString());
            cuenta.setSub_sub(registro[3].toString());
            cuenta.setColect(registro[4].toString());
            cuenta.cargarRegistro(conn);
            
            existe = !cuenta.getNom_cta().isEmpty();
            
            cuenta.setNom_cta(registro[5].toString());
            cuenta.setNivel(Short.parseShort(registro[6].toString()));
            cuenta.setTipo_cta(Short.parseShort(registro[7].toString()));
            
            if (existe){
                sqlUpdate =
                        "Update " + tablaMysql + " set " +
                        "   nom_cta = ?, " +
                        "   nivel   = ?, " +
                        "   tipo_cta = ? " +
                        "Where mayor = ? and sub_cta = ? and sub_sub = ? and colect  = ?";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setString(1, cuenta.getNom_cta());
                pr.setShort(2, cuenta.getNivel());
                pr.setShort(3, cuenta.getTipo_cta());
                pr.setString(4, cuenta.getMayor());
                pr.setString(5, cuenta.getSub_cta());
                pr.setString(6, cuenta.getSub_sub());
                pr.setString(7, cuenta.getColect());
            } else {
                sqlUpdate =
                        "Insert into " + tablaMysql + 
                        " (mayor, sub_cta, sub_sub, colect, nom_cta, nivel, tipo_cta) " +
                        "values(?,?,?,?,?,?,?)";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setString(1, cuenta.getMayor());
                pr.setString(2, cuenta.getSub_cta());
                pr.setString(3, cuenta.getSub_sub());
                pr.setString(4, cuenta.getColect());
                pr.setString(5, cuenta.getNom_cta());
                pr.setShort(6, cuenta.getNivel());
                pr.setShort(7, cuenta.getTipo_cta());
            }
            pr.executeUpdate();
        } // end while
        //CMD.transaction(conn, CMD.COMMIT);
        d.close();
    } // end main

} // end LeerDBFs
