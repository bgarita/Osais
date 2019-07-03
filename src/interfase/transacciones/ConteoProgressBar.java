package interfase.transacciones;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import java.awt.BorderLayout;
import java.awt.Container;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.Border;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita Azofeifa 12/04/2013
 * Esta clase se encarga de verifica y guardar los datos de un conteo físico.
 * Es invocada desde el form DigitacionConteo
 */
public class ConteoProgressBar extends Thread {
    private final JFrame frame;
    private final Container content;
    private final JProgressBar progressBar;
    private Border border;
    private final JTable tabla;
    private final ResultSet rsConteo;
    private final Statement stat;
    private final Connection conn;
    private int accion;
    public final static int GUARDARCONTEO  = 1;
    public final static int VERIFICARDATOS = 2;
    private String bodega;
    private final DigitacionConteo dc;
    
    public ConteoProgressBar(JTable tabla, ResultSet rs, Statement stat, DigitacionConteo d) {
        this.tabla = tabla;
        this.rsConteo = rs;
        this.stat = stat;
        this.accion = GUARDARCONTEO; // Acción predeterminada
        this.dc = d;
        this.conn = d.getConn();
        
        this.frame = new JFrame("Progress Bar");
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.content = this.frame.getContentPane();
        this.progressBar = new JProgressBar();
        int height = Ut.getProperty(Ut.OS_NAME).equals("Linux") ? 90: 65;
        int width = 400;
        this.frame.setSize(width, height);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setBounds((screenSize.width-width)/2, (screenSize.height-height)/2, width, height);
    } // end main
    
    public void setTitle(String title){
        frame.setTitle(title);
    } // end setTitle
    
    public void setVisible(boolean visible){
        frame.setVisible(visible);
    } // end setVisible
    
    public void setMaximumValue(int value){
        progressBar.setMaximum(value);
    } // end setMaximumValue
    
    public void setValue(int value){
        progressBar.setValue(value);
        progressBar.setStringPainted(true);
    } // end setValue
    
    public void setBorderTitle(String borderTitle){
        border = BorderFactory.createTitledBorder(borderTitle);
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
    } // end setBorderTitle
    
    public void setAccion(int a){
        this.accion = a;
    } // end seAccion
    
    public void setBodega(String bodega){
        this.bodega = bodega;
    } // end setBodega
    
    private void close(){
        setVisible(false);
        frame.dispose();
    }
    
    private void guardarConteo(){
        double cantidad, anterior;
        int linea, registrosAfectados, registrosGuardados = 0;
        String sqlUpdate, artdesc;
        
        // Recorrer toda la tabla para actualizar la base de datos
        for (int row = 0; row < tabla.getRowCount(); row++){
            setValue(row + 1);
            
            linea = Integer.parseInt(tabla.getValueAt(row, 0).toString());
            
            if (tabla.getValueAt(row, 3) == null || 
                    tabla.getValueAt(row, 3).toString().isEmpty()){
                cantidad = 0.00;
            } else {
                cantidad = Double.parseDouble(tabla.getValueAt(row, 3).toString());
            } // end if-else
            
            try {
                Ut.seek(rsConteo, linea, "linea");
                anterior = rsConteo.getDouble("anterior");
                artdesc  = rsConteo.getString("artdesc");
            } catch (SQLException ex) {
                Logger.getLogger(ConteoProgressBar.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                // Si se produjo un error paso al siguiente registro.
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                continue;
            } // end try-catch
            
            // Sólo se actualiza la base de datos si se modificó la cantidad
            if (cantidad == anterior){
                continue;
            } // end if
            
            sqlUpdate =
                    "Update conteo Set " +
                    "cantidad = " + cantidad + " " +
                    "Where linea = " + linea + " and " +
                    "cantidad = " + anterior + " and " +
                    "(InUseByUser = user() or InUseByUser = '')";
            try{
                // No hago uso de transacciones, cualquier registro
                // que se actualice aquí es ganancia y si alguno no
                // se actualizara por alguna razón el usuario lo
                // sabrá no solo por el error sino porque el sistem
                // cargará nuevamente los datos de la tabla.
                registrosAfectados = stat.executeUpdate(sqlUpdate);
                if (registrosAfectados == 0){
                    JOptionPane.showMessageDialog(null,
                            "No se pudo actualizar la línea # " +
                            linea + ".\n" +
                            "[" + artdesc.trim() + "]" + "\n" +
                            "Tal vez otro usuario la modificó" + "\n" +
                            "antes que usted.\n" +
                            "\nLa línea se cargará nuevamente.",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                } // end if
                registrosGuardados += registrosAfectados;
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                break;
            } // catch
        } // end for

        JOptionPane.showMessageDialog(null,
                registrosGuardados + " modificaciones guardadas.",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
        dc.cargarDatos();
    } // end 
    
    private void verificarConteo(){
        int linea;
        double cantidad;
        boolean guardado = true;
        boolean huboError = false;
        int salir = JOptionPane.YES_OPTION;
        for (int i = 0; i < tabla.getRowCount(); i++){
            setValue(i+1);
            if (tabla.getValueAt(i, 0) == null){
                continue;
            } // end if
            
            linea = Integer.parseInt(tabla.getValueAt(i, 0).toString());
            
            if (tabla.getValueAt(i, 3) == null || 
                    tabla.getValueAt(i, 3).toString().isEmpty()){
                cantidad = 0.00;
            } else {
                cantidad = Double.parseDouble(tabla.getValueAt(i, 3).toString());
            } // end if-else
            
            try {
                Ut.seek(rsConteo, linea, "linea");
                if (rsConteo.getDouble("anterior") != cantidad) {
                    guardado = false;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                huboError = true;
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end catch
            finally{
                if (!guardado){
                    salir = JOptionPane.showConfirmDialog(null,
                            "¡No ha guardado los datos!\n"+
                            "Si continúa perderá las cantidades digitadas.\n"+
                            "\n¿Desea continuar?",
                            "Advertencia",
                            JOptionPane.YES_NO_OPTION);
                } // end if

                // Esta parte del código solo correrá cuando se haya
                // producido un error capturado en el catch
                if (huboError && salir == JOptionPane.YES_OPTION){
                    dc.dispose();
                    break;
                } // end if

                // Si ya se encontró algún registro sin guardar no tiene
                // caso revisar más.
                if (!guardado){
                    break;
                } // end if
            } // end finally
        } // end for

        // Esta parte del código no correrá si ocurre algún error en el for
        if (salir == JOptionPane.YES_OPTION){
            // Liberar los registros
            String sqlUpdate =
                    "Update conteo Set InUseByUser = '' "   +
                    "Where bodega = " + "'" + bodega + "' " +
                    "and InUseByUser = user()";
            try {
                UtilBD.SQLUpdate(conn, sqlUpdate);
            } catch (SQLException ex) {
                Logger.getLogger(DigitacionConteo.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, 
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end try-catch
            dc.dispose();
        } // end if
    } // end verificarConteo
    
    @Override
    public void run(){
        if (this.accion == ConteoProgressBar.GUARDARCONTEO){
            guardarConteo();
            close();
        } else {
            verificarConteo();
            close();
        } // end if-else
    }
} // end ProgressBar
