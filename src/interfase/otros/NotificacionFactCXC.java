package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.consultas.DetalleNotificacion2;
import interfase.menus.Menu;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author bosco
 */
public class NotificacionFactCXC extends Thread{
    private final Connection conn;
    // Variable que define si notifica o no.
    private boolean notifFactcxc;
    
    // Variable para determinar el intervalo de notificaciones.
    private short intervalo2;
    
    private long espera;
    private boolean detenido;
    private final Bitacora b = new Bitacora();
    
    public NotificacionFactCXC(Connection c, boolean n){
        this.conn = c;
        this.notifFactcxc = n;
        this.detenido = false;
    } // end constructor
    
    public void detener(){
        this.detenido = true;
    } // end detener
    
    
    private void cargarIntervalo(){
        if (detenido){
            return;
        } // end if
        
        ResultSet rs;
        String sqlSent;
        sqlSent = 
                "Select " +
                "   notifFactcxc, intervalo2 " +
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
                this.notifFactcxc = rs.getBoolean("notifFactcxc");
                this.intervalo2   = rs.getShort("intervalo2");
            } // end if
            ps.close();
            
            // Establecer el intervalo
            espera = intervalo2;
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
    
    
    private void notificar(){
        if (detenido){
            return;
        } // end if
        
        /*
         * 1. Obtener la lista de las facturas vencidas o por 
         *    vencer (5 días).
         * 2. Informar sobre la situación y actualizar la tabla
         *    notificado.
         */
        String sqlSent = 
                "Select " +
                "	inclient.clidesc," +
                "	faencabe.facnume," +
                "	faencabe.facmont," +
                "	faencabe.facsald," +
                "	dtoc(faencabe.facfepa) as facfepa," + 
                "	Case " +
                "		When datediff(now(),faencabe.facfepa) > 0 then 'Vencida'" +
                "		When datediff(now(),faencabe.facfepa) = 0 then 'Vence hoy'" +
                "		Else Concat('Vence en ', abs(datediff(now(),faencabe.facfepa)), ' días') " +
                "	End as estado " +
                "From faencabe " +
                "Inner join inclient on faencabe.clicode = inclient.clicode " +
                "Where faencabe.facsald > 0 " +
                "and faencabe.facnd = 0 " +
                "and datediff(now(),faencabe.facfepa) >= -8 " +
                "and faencabe.facestado = '' " +
                "order by faencabe.facfepa";
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
                DetalleNotificacion2.main(rs);
                // Guardar los registros de la notificación
                rs.beforeFirst();
                while (!detenido && !rs.isClosed() && rs.next()){
                    UtilBD.actualizarNotificaciones(
                            conn, 
                            "Factura vencida o por vencer", 
                            2, // El ID para las facruras de CXC es 2.
                            rs.getString("facnume"), 
                            "CXC", 
                            Menu.BASEDATOS);
                } // end while
            } // end if
            ps.close();
        } catch (SQLException | NullPointerException | ConcurrentModificationException ex) {
            Logger.getLogger(NotificacionFactCXC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end notificar
    
    @Override
    public void run(){
        if (this.detenido){
            return;
        } // end if
        cargarIntervalo();
        long tiempo = 500;
        while (!detenido && notifFactcxc){
            notificar();
            while (!detenido && espera > tiempo){
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, 
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(NotificacionFactCXC.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                } // end try-catch
                espera += 500;
            } // end while
            
            cargarIntervalo();
            tiempo = 500;
        } // end while
    } // end run
} // end NotificacionMinimos
