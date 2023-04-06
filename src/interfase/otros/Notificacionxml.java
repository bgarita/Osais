package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.consultas.DetalleNotificacionXml;
import interfase.menus.Menu;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.DocumentoElectronico;
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
    private final Bitacora b = new Bitacora();

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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
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
     * 5 (Aceptado, rechazado) de hace un mes, ejecuta la consulta en Hacienda y
     * vuelve a actualizar la tabla faestadoDocElect (se genera el xml firmado).
     */
    private void actualizarEstados() {
        // Este proceso es únicamente windows por lo que no debe correr en Linux
        String os = Ut.getProperty(Ut.OS_NAME).toLowerCase();
        if (!os.contains("win") || !Menu.enviarDocumentosElectronicos) {
            return;
        } // end if

        String sqlSent
                = "Select     "
                + "   a.facnume,     "
                + "   a.facnd,       "
                + " 	a.referencia, "
                + " 	CASE    "
                + " 		When a.facnd = 0 then 'FAC'    "
                + " 		When a.facnd > 0 then 'NCR'    "
                + " 		When a.facnd < 0 then 'NDB'    "
                + " 		Else 'N/A'    "
                + " 	END as tipo  "
                + " from faestadoDocElect a   "
                + " INNER JOIN faencabe b ON a.facnume = b.facnume AND a.facnd = b.facnd "
                + " WHERE a.estado not in (4,5) "
                + " AND b.facfechac >= DATE_SUB(CURDATE(),INTERVAL 1 MONTH)";

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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        try {
            DocumentoElectronico docEl;
            while (rs != null && rs.next()) {
                docEl = new DocumentoElectronico(rs.getInt("facnume"), rs.getInt("facnd"), "V", conn, "2");
                docEl.enviarXML();
            } // end while
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Notificacionxml.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end actualizarEstados
} // end Notificacionxml
