package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.consultas.DetalleNotificacionXml;
import interfase.menus.Menu;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 * Este proceso no solo manda las alertas sobre los documentos electrónicos,
 * también actualiza la tabla de los estados antes de enviar las alertas.
 *
 * @author bosco
 *
 */
public class Notificacionxml extends Thread {

    private final Connection conn;
    // Variable que define si notifica o no.
    private boolean notificacion;

    // Variable para determinar el intervalo de notificaciones.
    private short intervalo;

    private long espera;
    private boolean detenido;

    public Notificacionxml(Connection c, boolean notificacion) {
        this.conn = c;
        this.notificacion = notificacion;
        this.detenido = false;
    } // end constructor

    public void detener() {
        this.detenido = true;
    } // end detener

    private void cargarIntervalo() {
        if (detenido) {
            return;
        } // end if

        ResultSet rs;
        String sqlSent;
        sqlSent
                = "Select "
                + "   notifxmlfe, intervalo4 "
                + "From usuario "
                + "Where user = GetDBUser()";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(
                    sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                this.notificacion = rs.getBoolean("notifxmlfe");
                this.intervalo = rs.getShort("intervalo4");
            } // end if
            ps.close();

            // Establecer el intervalo
            espera = intervalo;
            espera = espera * 60 * 1000; // Milisegundos
        } catch (SQLException ex) {
            Logger.getLogger(Notificacion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    "[Notificaciones automáticas] " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarIntervalo

    /**
     * Primero actualiza el estado de los documentos y luego muestra los
     * resultados.
     */
    private void notificar() {
        if (detenido) {
            return;
        } // end if

        // Enviar las consultas de todos los documentos cuyo estado no sea 4 ó 5.
        actualizarEstados();

        DetalleNotificacionXml dnx = new DetalleNotificacionXml(new javax.swing.JFrame(), true, conn);
        dnx.setVisible(true);
//        
//        /*
//         * 1. Obtener la lista de documentos enviados a Hacienda cuyo estado
//              aún no haya sido informado al usuario.
//         * 2. Informar sobre la situación y actualizar la tabla notificado.
//         */
//        String sqlSent
//                = "Select     "
//                + "	a.facnume,  "
//                + "	a.facnd,    "
//                + "	a.xmlFile,  "
//                + "	a.estado,   "
//                + "	a.descrip,  "
//                + "	a.correo,   "
//                + "	a.fecha,    "
//                + "	CASE        "
//                + "		When a.facnd = 0 then 'FAC'    "
//                + "		When a.facnd > 0 then 'NCR'    "
//                + "		When a.facnd < 0 then 'NDB'    "
//                + "		Else 'N/A'    "
//                + "	END as tipo,  "
//                + "	If (c.clidesc is null, concat('* ', e.prodesc), c.clidesc) as clidesc,  "
//                + "	a.referencia,  "
//                + "	a.xmlEnviado,  "
//                + "	a.emailDestino, "
//                + "	a.fechaEnviado, "
//                + "     a.tipoxml "
//                + "from faestadoDocElect a  "
//                + "Left JOIN faencabe b on  "
//                + "	a.facnume = b.facnume and a.facnd = b.facnd  "
//                + "Left join inclient c ON  "
//                + "	b.clicode = c.clicode  "
//                + "Left join cxpfacturas d on  "
//                + "	a.facnume = d.factura and d.tipo = 'FAC' "
//                + "Left join inproved e ON "
//                + "	d.procode = e.procode "
//                + "Where informado = 'N'  "
//                + "order by fecha desc";
//        ResultSet rs;
//        PreparedStatement ps, ps2;
//        try {
//            ps = conn.prepareStatement(
//                    sqlSent,
//                    ResultSet.TYPE_SCROLL_SENSITIVE,
//                    ResultSet.CONCUR_READ_ONLY);
//            rs = CMD.select(ps);
//            // Invocar un form de tipo modal y allways on top 
//            // desde aquí pasándole el ResultSet
//            // para que lo muestre en un JTable.
//            if (rs != null && rs.first()) {
//                // Por ahora solo se muestra en pantalla pero queda listo
//                // para enviar la alerta por correo si se necesita.
//                DetalleNotificacionXml.main(rs);
//
//                // Guardar los registros de la notificación
//                sqlSent = "Update faestadoDocElect "
//                        + "     Set informado = 'S' "
//                        + "Where facnume = ? "
//                        + "and facnd = ?";
//                ps2 = conn.prepareStatement(sqlSent);
//
//                rs.beforeFirst();
//                while (!detenido && !rs.isClosed() && rs.next()) {
//                    UtilBD.actualizarNotificaciones(
//                            conn,
//                            "Estado XML:\n" + rs.getString("descrip"),
//                            4, // El ID para los xmls es 4. (`saisystem`.`notificacion`)
//                            rs.getString("facnume"),
//                            rs.getString("tipo"),
//                            Menu.BASEDATOS);
//
//                    // Si el estado es 4 (aceptado) o 5 (rechazado), se actualiza 
//                    // el campo informado para que no se vuelva a notificar.
//                    if (rs.getInt("estado") == 4 || rs.getInt("estado") == 5) {
//                        ps2.setInt(1, rs.getInt("facnume"));
//                        ps2.setInt(2, rs.getInt("facnd"));
//                        CMD.update(ps2);
//                    } // end if
//                } // end while
//                ps2.close();
//            } // end if
//            ps.close();
//        } catch (SQLException | NullPointerException | ConcurrentModificationException ex) {
//            Logger.getLogger(Notificacionxml.class.getName()).log(Level.SEVERE, null, ex);
//            JOptionPane.showMessageDialog(null,
//                    ex.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
//        } // end try-catch
//        
    } // end notificar

    @Override
    public void run() {
        if (this.detenido) {
            return;
        } // end if
        cargarIntervalo();
        long tiempo = 5000;
        while (!detenido && notificacion) {
            notificar();

            // Este while sostiene la espera hasta que se cumpla el tiempo
            // para notificar de nuevo.
            while (!detenido && espera > tiempo) {
                try {
                    sleep(5000); // Espera 5 segundos y vuelve a revisar
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(Notificacionxml.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                } // end try-catch
                tiempo += 5000;
            } // end while

            // Se vuelve a cargar el intervalo y el parámetro que indica si se
            // debe notificar o no.  Esto se hace por si durante el tiempo de
            // espera entre notificaciones se hizo algún cambio a nivel de
            // configuración de las notificaciones.
            cargarIntervalo();
            tiempo = 500;
        } // end while
    } // end run

    /**
     * Este método toma todos los documentos electrónicos cuyo estado no sea 4 ó
     * 5 (Aceptado, rechazado), ejecuta la consulta en Hacienda y vuelve a
     * actualizar la tabla faestadoDocElect (se genera el xml firmado).
     */
    private void actualizarEstados() {
        // Este proceso es únicamente windows por lo que no debe correr en Linux
        String os = Ut.getProperty(Ut.OS_NAME).toLowerCase();
        if (!os.contains("win") || !Menu.enviarDocumentosElectronicos) {
            return;
        } // end if

        String sqlSent
                = "Select    "
                + "     a.facnume,    "
                + "	a.referencia, "
                + "	CASE   "
                + "		When a.facnd = 0 then 'FAC'   "
                + "		When a.facnd > 0 then 'NCR'   "
                + "		When a.facnd < 0 then 'NDB'   "
                + "		Else 'N/A'   "
                + "	END as tipo "
                + "from faestadoDocElect a "
                + "Where estado not in (4,5)";

        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = conn.prepareStatement(
                    sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Notificacionxml.class.getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        String dirXMLS = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);
        String cmd;

        try {
            while (rs != null && rs.next()) {
                cmd = dirXMLS + "EnviarFactura2.exe "
                        + rs.getString("referencia") + " "
                        + rs.getString("facnume") + " 2 " + rs.getString("tipo");
                Process p = Runtime.getRuntime().exec(cmd);
            } // end while
            ps.close();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(Notificacionxml.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end actualizarEstados
} // end Notificacionxml
