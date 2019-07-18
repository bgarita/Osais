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

    public static void update(Connection conn) throws SQLException {
        String sqlSent;
        PreparedStatement ps;

        // Si la versión inicial es mayor que la versión del sistema 
        // entonces no hago nada.
        if (INITIAL_VERSION.compareTo(Menu.VERSIONN) > 0) {
            return;
        }

        // Si el campo no existe lo agrego
        if (!UtilBD.fieldInTable(conn, "sincronizarTablas", "config")) {
            sqlSent
                    = "ALTER TABLE `config` "
                    + "ADD COLUMN `sincronizarTablas` TINYINT(1) NOT NULL DEFAULT '0' AFTER `genmovcaja`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if (!UtilBD.fieldInTable(conn, "sincronizarTablas", "config"))

        // Modificar la tabla de configuración para que el sistema sea parametrizable
        // en cuanto enviar o no enviar las facturas electrónicas a Hacienda.
        if (!UtilBD.fieldInTable(conn, "enviarFacturaE", "config")) {
            sqlSent
                    = "ALTER TABLE `config` "
                    + "ADD COLUMN `enviarFacturaE` TINYINT(1) NOT NULL DEFAULT 1 "
                    + "COMMENT 'Enviar facturas electrónicas 1=Si, 0=No' AFTER `tiqElect`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if (!UtilBD.fieldInTable(conn, "enviarFacturaE", "config"))

        // Bosco agregado 18/07/2019.  Elimino varios índices y agrego uno.
        if (UtilBD.indexInDB(conn, "FK_Hinmovimd_bodexis")) {
            sqlSent
                    = "ALTER TABLE `inmovimd`  "
                    + "        DROP INDEX `FK_Hinmovimd_bodexis`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if

        if (UtilBD.indexInDB(conn, "Index_Movdocu_H")) {
            sqlSent
                    = "ALTER TABLE `inmovimd`  "
                    + "        DROP INDEX `Index_Movdocu_H`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if

        if (UtilBD.indexInDB(conn, "FK_Hinmovimd_inmovime")) {
            sqlSent
                    = "ALTER TABLE `inmovimd`  "
                    + "        DROP INDEX `FK_Hinmovimd_inmovime`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if
        
        if (UtilBD.indexInDB(conn, "FK_Hinmovim_Tipocambio")) {
            sqlSent
                    = "ALTER TABLE `inmovime`  "
                    + "        DROP INDEX `FK_Hinmovim_Tipocambio`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if
        
        if (!UtilBD.indexInDB(conn, "Index_recalcular_inv")) {
            sqlSent
                    = "ALTER TABLE `inmovime`  "
                    + "        ADD INDEX `Index_recalcular_inv` (`movfech` ASC, `estado` ASC)";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if
        
        if (UtilBD.indexInDB(conn, "fk_hbodexis_hintarticu")) {
            sqlSent
                    = "ALTER TABLE `hbodexis`  "
                    + "        DROP FOREIGN KEY `fk_hbodexis_hintarticu`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if
        
        if (UtilBD.indexInDB(conn, "FK_hbodexis_bodexis")) {
            sqlSent
                    = "ALTER TABLE `hbodexis`  "
                    + "        DROP INDEX `FK_hbodexis_bodexis`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if
        // Fin Bosco agregado 18/07/2019
    } // end update
} // end UpdateVersion
