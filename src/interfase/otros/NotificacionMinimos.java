package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.consultas.DetalleNotificacion;
import interfase.menus.Menu;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author bosco
 */
public class NotificacionMinimos extends Thread{
    private final Connection conn;
    // Variable que define si notifica o no.
    private boolean notifcompra;
    
    // Variable para determinar el intervalo de notificaciones.
    private short intervalo1;
    
    private long espera;
    
    private boolean detenido;
    private final Bitacora b = new Bitacora();
    
    public NotificacionMinimos(Connection c, boolean n){
        this.conn = c;
        this.notifcompra = n;
        this.detenido = false;
    } // end constructor
    
    public void detener(){
        this.detenido = true;
    }
    
    private void cargarIntervalo(){
        if (this.detenido){
            return;
        } // end if
        
        ResultSet rs;
        String sqlSent;
        sqlSent = 
                "Select " +
                "   notifcompra, intervalo1 " +
                "From usuario " +
                "Where user = GetDBUser()";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(
                    sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs != null && rs.first()){
                this.notifcompra  = rs.getBoolean("notifcompra");
                this.intervalo1   = rs.getShort("intervalo1");
            } // end if
            ps.close();
            
            // Establecer el intervalo
            espera = intervalo1;
            espera = espera * 60 * 1000; // Milisegundos
        } catch (SQLException ex) {
            Logger.getLogger(Notificacion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    "[Notificaciones automáticas] " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarIntervalo
    
    
    private void notificar(){
        if (this.detenido){
            return;
        } // end if
        /*
         * 1. Obtener la lista de los artículos igual o bajo el
         *    mínimo exluyendo las excepciones.
         * 2. Informar sobre la situación y actualizar la tabla
         *    notificado.
         */
        String sqlSent = 
                "Select " +
                "	bodexis.artcode, " +
                "	bodexis.bodega,  " +
                "       inarticu.artdesc," +
                "	bodexis.artexis, " +
                "	bodexis.minimo   " +
                "From bodexis    " +
                "Inner join inarticu on bodexis.artcode = inarticu.artcode " +
                "Where bodexis.minimo > 0 " +
                "and bodexis.artexis <= bodexis.minimo " +
                "and not exists( " +
                "	Select codigo " +
                "	from saisystem.excepcion_notif " +
                "	Where codigo = bodexis.artcode " +
                "	and bodega = bodexis.bodega)";
        ResultSet rs;
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(
                    sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            // Invocar un form de tipo modal y allways on top 
            // desde aquí pasándole el ResultSet
            // para que lo muestre en un JTable.
            if (rs != null && rs.first()){
                DetalleNotificacion.main(rs);
                // Guardar los registros de la notificación
                rs.beforeFirst();
                while (rs.next() && !detenido){
                    UtilBD.actualizarNotificaciones(
                            conn, 
                            "Artículo en o bajo el mínimo", 
                            1, // El ID para mínimos es uno.
                            rs.getString("artcode"), 
                            rs.getString("bodega"), 
                            Menu.BASEDATOS);
                } // end while
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(NotificacionMinimos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end notificar
    
    @Override
    public void run(){
        if (this.detenido){
            return;
        } // end if
        cargarIntervalo();
        long tiempo = 500;
        while (notifcompra && !detenido){
            notificar();
            while (!detenido && espera > tiempo){
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, 
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(NotificacionMinimos.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                } // end try-catch
                espera += 500;
            } // while
            
            cargarIntervalo();
            tiempo = 500;
        } // end while
    } // end run
} // end NotificacionMinimos
