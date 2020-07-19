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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco, 11/04/2016
 */
public class UpdateVersion {

    public static String INITIAL_VERSION = "2.8r0";
    
    

    public static void update(Connection conn) throws SQLException {
        firstUpdate(conn);

        if (Menu.VERSIONN.equals("4.5r2")) {
            update45r2(conn);
        } // end if
    } // end update

    
    private static void firstUpdate(Connection conn) throws SQLException {
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
        double DBVersionNumber
                = Double.parseDouble(Ut.quitarCaracteres(Menu.dataBaseVersion, ".").toString());

        if (DBVersionNumber >= 5.7 && UtilBD.indexInDB(conn, "FK_Hinmovimd_bodexis")) {
            sqlSent
                    = "ALTER TABLE `inmovimd`  "
                    + "        DROP INDEX `FK_Hinmovimd_bodexis`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if

        if (DBVersionNumber >= 5.7 && UtilBD.indexInDB(conn, "Index_Movdocu_H")) {
            sqlSent
                    = "ALTER TABLE `inmovimd`  "
                    + "        DROP INDEX `Index_Movdocu_H`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if

        if (DBVersionNumber >= 5.7 && UtilBD.indexInDB(conn, "FK_Hinmovimd_inmovime")) {
            sqlSent
                    = "ALTER TABLE `inmovimd`  "
                    + "        DROP INDEX `FK_Hinmovimd_inmovime`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if

        if (DBVersionNumber >= 5.7 && UtilBD.indexInDB(conn, "FK_Hinmovim_Tipocambio")) {
            sqlSent
                    = "ALTER TABLE `inmovime`  "
                    + "        DROP INDEX `FK_Hinmovim_Tipocambio`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if

        if (DBVersionNumber >= 5.7 && !UtilBD.indexInDB(conn, "Index_recalcular_inv")) {
            sqlSent
                    = "ALTER TABLE `inmovime`  "
                    + "        ADD INDEX `Index_recalcular_inv` (`movfech` ASC, `estado` ASC)";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if

        if (DBVersionNumber >= 5.7 && UtilBD.indexInDB(conn, "fk_hbodexis_hintarticu")) {
            sqlSent
                    = "ALTER TABLE `hbodexis`  "
                    + "        DROP FOREIGN KEY `fk_hbodexis_hintarticu`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if

        if (DBVersionNumber >= 5.7 && UtilBD.indexInDB(conn, "FK_hbodexis_bodexis")) {
            sqlSent
                    = "ALTER TABLE `hbodexis`  "
                    + "        DROP INDEX `FK_hbodexis_bodexis`";

            ps = conn.prepareStatement(sqlSent);
            ps.execute();
            ps.close();
        } // end if
        // Fin Bosco agregado 18/07/2019

        // ** Actualización de la tabla de opciones (tareas del sistema)
        // --------------------------------------------------------------
        sqlSent
                = "SELECT * FROM programa "
                + "WHERE programa = ?";
        HashMap<String, String> tareas = new HashMap<>();
        tareas.put("ReferenciaNotaCXC", "Referenciar notas de crédito.");
        tareas.put("ConsultaMovCierre", "Movimientos auxiliares");

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        // Hacer un recorrido agregando las tareas que faltan.
        String sqlInsert
                = "INSERT INTO programa (programa, descrip) "
                + "	VALUES( ?,?)";
        String program;
        Iterator<String> it = tareas.keySet().iterator();
        while (it.hasNext()) {
            program = it.next();
            ps.setString(1, program);
            if (UtilBD.existeRegistro(ps, true)) { // Revisa si hay registros y cierra el RS
                continue;
            } // end if

            // Si el registro no existe lo agrego.
            PreparedStatement ps2 = conn.prepareStatement(sqlInsert);
            ps2.setString(1, program);
            ps2.setString(2, tareas.get(program));
            CMD.update(ps2);
            ps2.close();
        } // end while
        // --------------------------------------------------------------

        // Validar el ancho de las columnas de correo electrónico.
        double columnLength = UtilBD.columnLength(conn, "correo", "faestadoDocElect");
        if (columnLength > 0 && columnLength < 100) {
            sqlSent
                    = "ALTER TABLE `faestadoDocElect` "
                    + "	CHANGE COLUMN `correo` `correo` VARCHAR(100) NOT NULL DEFAULT ' ' "
                    + " COMMENT 'Dirección de correo electrónico a la que fue enviada la notificación.' AFTER `informado`, "
                    + "	CHANGE COLUMN `emailDestino` `emailDestino` VARCHAR(100) NOT NULL DEFAULT ' ' "
                    + " COMMENT 'Correo electrónico al que se envió el xml' AFTER `fechaEnviado`";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();

            sqlSent
                    = "ALTER TABLE `inclient` "
                    + "	CHANGE COLUMN `cliemail` `cliemail` VARCHAR(100) NULL DEFAULT NULL COMMENT 'Correo electrónico' AFTER `clicelu`";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();

            sqlSent
                    = "ALTER TABLE `hinclient` "
                    + "	CHANGE COLUMN `cliemail` `cliemail` VARCHAR(100) NULL DEFAULT NULL COMMENT 'Correo electrónico' AFTER `clicelu`";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();

            sqlSent
                    = "ALTER TABLE `inproved` "
                    + "	CHANGE COLUMN `email` `email` VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Correo electrónico' AFTER `colect`";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();

            sqlSent
                    = "ALTER TABLE `hinproved` "
                    + "	CHANGE COLUMN `email` `email` VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Correo electrónico' AFTER `colect`";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();

            sqlSent
                    = "ALTER TABLE `config` "
                    + "	CHANGE COLUMN `correoE` `correoE` VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Correo electrónico' AFTER `docElectProv`";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();
        } // end if
    } // firstUpdate
    
    private static void update45r2(Connection conn) throws SQLException {
        // Hago una prueba simple para determinar si existe un campo propio
        // de esta actualización.  So existe no se debe ejecutar.
        if (UtilBD.fieldInTable(conn, "codigoTarifa", "inarticu")) {
            return;
        } // end if
        
        String sqlSent;
        PreparedStatement ps;

        // Agrear la opción para respaldos de archivos
        sqlSent = "INSERT INTO `programa`(`programa`, `descrip`)"
                + "	VALUES('RespaldoArchivosSistema', 'Respaldar archivos del sistema (xml, pdf, etc)')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        // Mover el campo usarivi para que tenga más sentido al desplegarlo
        sqlSent = "ALTER TABLE `config` "
                + "	CHANGE COLUMN `usarivi` `usarivi` TINYINT(1) UNSIGNED NOT NULL DEFAULT '1'  "
                + "     COMMENT 'Usar precios con impuesto incluido (0=No, 1=Si)' AFTER `ndeb`";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        // Crear la tabla de tarifas según el Ministerio de Hacienda.
        sqlSent = "CREATE TABLE `tarifa_iva` ( "
                + "	`codigoTarifa` VARCHAR(3) NOT NULL COMMENT 'Código de tarifa', "
                + "	`descrip` VARCHAR(30) NOT NULL COMMENT 'Descripción de la tarifa', "
                + "	`porcentaje` FLOAT(12) NOT NULL DEFAULT '0' COMMENT 'Porcentaje de la tarifa', "
                + "	PRIMARY KEY (`codigoTarifa`) USING BTREE "
                + ") "
                + "COMMENT='Catálogo de impuestos según el Ministerio de Hacienda' "
                + "COLLATE='latin1_swedish_ci' "
                + "ENGINE=InnoDB";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        // Agrear los datos
        sqlSent = "INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`) VALUES ('01', 'Tarifa 0% (Exento)')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('02', 'Tarifa reducida 1%', '1')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('03', 'Tarifa reducida 2%', '2')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('04', 'Tarifa reducida 4%', '4')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`) VALUES ('05', 'Transitorio 0%')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('06', 'Transitorio 4% ', '4')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('07', 'Transitorio 8%', '8')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "INSERT INTO `tarifa_iva` (`codigoTarifa`, `descrip`, `porcentaje`) VALUES ('08', 'Tarifa general 13%', '13')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        // Agregar el nuevo campo en el catálogo de artículos y otras tablas y relacionarlas con el catálogo de tarifas
        sqlSent = "ALTER TABLE inarticu "
                + "	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda'";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE `inarticu` "
                + "	ADD CONSTRAINT `FK_inarticu_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE hinarticu "
                + "	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda'";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE `hinarticu` "
                + "	ADD CONSTRAINT `FK_hinarticu_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE inarticu_sinc "
                + "	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda'";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE `inarticu_sinc` "
                + "	ADD CONSTRAINT `FK_inarticu_sinc_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE fadetall "
                + "	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda'";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE `fadetall` "
                + "	ADD CONSTRAINT `FK_fadetall_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE wrk_fadetall "
                + "	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda'";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE `wrk_fadetall` "
                + "	ADD CONSTRAINT `FK_wrk_fadetall_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE pedidod "
                + "	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda'";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE `pedidod` "
                + "	ADD CONSTRAINT `FK_pedidod_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE pedidofd "
                + "	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda'";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE `pedidofd` "
                + "	ADD CONSTRAINT `FK_pedidofd_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE salida "
                + "	ADD codigoTarifa VARCHAR(3) NOT NULL DEFAULT '01' comment 'Código de tarifa según Hacienda'";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);

        sqlSent = "ALTER TABLE `salida` "
                + "	ADD CONSTRAINT `FK_salida_tarifa_iva` FOREIGN KEY (`codigoTarifa`) REFERENCES `tarifa_iva` (`codigoTarifa`) ON UPDATE CASCADE";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);
        
        // Agregar la opción de impuestos
        sqlSent = "INSERT INTO `programa` (`programa`, `descrip`) VALUES ('Impuestos', 'Mantenimiento de impuestos')";
        ps = conn.prepareStatement(sqlSent);
        CMD.update(ps);
    } // end update45r2
} // end UpdateVersion
