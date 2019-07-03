/*
 * Esta clase valida los posibles cambios en base de datos para realizar
 * las actualizaciones que sean necesarias.  Los cambios pueden ser a nivel
 * de procedimientos almacenados, funciones, triggers, tablas, etc. En otras
 * palabras, actualiza cualquier objeto de base de datos e inclusive podría
 * modificar datos cuando así lo requiera.
 */
package accesoDatos;

import interfase.menus.Menu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author bosco, 11/04/2016
 */
public class UpdateVersion {
    public static String INITIAL_VERSION = "2.8r0";
    
    
    
    public static void update(Connection conn) throws SQLException{
        String sqlSent;
        PreparedStatement ps;
        
        // Si la versión inicial es mayor que la versión del sistema 
        // entonces no hago nada.
        if (INITIAL_VERSION.compareTo(Menu.VERSIONN) > 0){
            return;
        }
        
        // Si el campo no existe lo agrego
        if (!UtilBD.fieldInTable(conn, "sincronizarTablas", "config")){
            sqlSent = 
                    "ALTER TABLE `config` " +
                    "ADD COLUMN `sincronizarTablas` TINYINT(1) NOT NULL DEFAULT '0' AFTER `genmovcaja`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if (!UtilBD.fieldInTable(conn, "sincronizarTablas", "config"))
        
    } // end update
} // end UpdateVersion
