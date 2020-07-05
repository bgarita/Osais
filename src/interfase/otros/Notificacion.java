package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
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
public class Notificacion extends Thread {
    private final Connection conn;
    // Variables que define si notifica o no.
    private boolean notifcompra, notifFactcxc, notifFactcxp, notifxmlfe;
    private boolean detenido;
    private NotificacionMinimos nm;
    private NotificacionFactCXC cxc;
    private Notificacionxml nXml;
    private final Bitacora b = new Bitacora();
    
    // Constructor
    public Notificacion(Connection c) {
        this.conn = c;
        this.detenido = false;
    } // end constructor
    
    public void detener(){
        this.detenido = true;
        if (nm != null){
            nm.detener();
        } // end if
        if (cxc != null){
            cxc.detener();
        } // end if
        
    } // end detener
    
    private void cargarNotificaciones(){
        ResultSet rs;
        String sqlSent;
        sqlSent = 
                "Select " +
                "   notifcompra, notifFactcxc, notifFactcxp, notifxmlfe  " +
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
                this.notifFactcxc = rs.getBoolean("notifFactcxc");
                this.notifFactcxp = rs.getBoolean("notifFactcxp");
                this.notifxmlfe   = rs.getBoolean("notifxmlfe");
            } // end if
            ps.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Notificacion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    "[Notificaciones automáticas] " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarNotificaciones
    
    
    @Override
    public void run(){
        if (this.detenido){
            nm.detener();
            cxc.detener();
        } // end if
        
        cargarNotificaciones();
        
        // Notificar sobre los artículos bajo el mínimo
        if (notifcompra){
            nm = new NotificacionMinimos(conn, true);
            nm.start();
        } // end if
        
        // Notificar sobre los facturas de venta vencidas y por vencer.
        // Este proceso muestra todas las facturas vencidas y aquellas
        // que les quedan 5 días o menos para vencer.
        if (notifFactcxc){
            cxc = new NotificacionFactCXC(conn, true);
            cxc.start();
        } // end if
        
        // Notificar sobre los facturas de compra vencidas y por vencer.
        // Este proceso muestra todas las facturas vencidas y aquellas
        // que les quedan 5 días o menos para vencer.
//        if (notifFactcxp){
//            NotificacionFactCXP cxp = new NotificacionFactCXP(conn, true);
//            cxp.start();
//        } // end if
        
        // Notificar sobre el estado de los xmls enviados a Hacienda.
        if (notifxmlfe){
            this.nXml = new Notificacionxml(conn, true);
            nXml.start();
        } // end if
    } // end run
} // end class
