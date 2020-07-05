package logica.utilitarios;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window.Type;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 *
 * @author Bosco Garita Azofeifa 10/07/2015
 * Esta clase se encarga (por ahora) de generar ejecutar el cierre
 * mensual dentro de un hilo separado.
 * 
 */
public class ProcessProgressBar extends Thread {
    private final JFrame frame;
    private final Container content;
    private final JProgressBar progressBar;
    private Border border;
    private final javax.swing.JLabel lblInfo;
    
    private final Connection conn;
    private CallableStatement cs;
    private int mes;
    private int ano;
    
    private final String windowTitle;
    private final Bitacora b = new Bitacora();
    
    public ProcessProgressBar(
            Connection c, 
            String windowTitle ) {
        this.conn = c;
        this.windowTitle = windowTitle;
        
        this.frame = new JFrame(windowTitle);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setType(Type.UTILITY);
        this.content = this.frame.getContentPane();
        this.progressBar = new JProgressBar();
        this.lblInfo = new javax.swing.JLabel();
        
        this.lblInfo.setFont(new java.awt.Font("Ubuntu", 1, 15));
        this.lblInfo.setForeground(java.awt.Color.blue);
        this.lblInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        this.lblInfo.setPreferredSize(new Dimension(750,18));
        this.lblInfo.setText("Iniciando..");
        
        
        setBorderTitle("Procesando...");
        
        this.progressBar.setMinimum(0);
        this.progressBar.setBorderPainted(true);
        this.progressBar.setStringPainted(true);
        this.progressBar.setOrientation(JProgressBar.HORIZONTAL);
        this.content.add(progressBar, BorderLayout.NORTH);
        this.content.add(lblInfo, BorderLayout.SOUTH);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
    } // end main

    public void setLblInfoText(String text) {
        this.lblInfo.setText(text);
    }

    
    public void setCs(CallableStatement cs) {
        this.cs = cs;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }
    
    
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
    
    public int getValue(){
        return this.progressBar.getValue();
    } // end getValue
    
    public final void setBorderTitle(String borderTitle){
        border = BorderFactory.createTitledBorder(borderTitle);
        progressBar.setBorder(border);
    } // end setBorderTitle
    
    
    public void close(){
        setVisible(false);
        frame.dispose();
    }
    
    private void ejecutarCierreMensual(){
        if (this.cs == null){
            return;
        }
        
        boolean hayTransaccion = false;
        try {
            // Iniciar la transacción
            CMD.transaction(conn, CMD.START_TRANSACTION);
            hayTransaccion = true;
            
            // Registrar los parámetros de entrada
            cs.setInt(1, mes);
            cs.setInt(2, ano);

            // Registrar los parámetros de salida
            cs.registerOutParameter( 3, Types.BOOLEAN);
            cs.registerOutParameter( 4, Types.VARCHAR);

            boolean error = false;
            // El cierre tiene 12 etapas
            /*
            1.  Verifico que todas las facturas tengan su respectivo detalle.
            2.  Verifico que todos los documentos de inventario tengan su respectivo detalle.
            3.  Eliminar posibles inconsistencias.
            4.  Recalcular el inventario reservado.
            5.  Establecer el saldo de los registros de CXC (fact, nc, nd) a la fecha de cierre.
            6.  Establecer el saldo de los clientes a la fecha de cierre.
            7.  Recalcular las existencias a la fecha de cierre.
            8.  Trasladar los datos a la tablas históricas.
            9.  Marcar las facturas, NC y ND con saldo cero correspondientes al cierre como cerradas.
            10. Marcar todos los registros de CXC en inventarios como cerrados.
            11. Marcar todos los movimientos de inventarios como cerrados.
            12. Marcar recibos de CXC y CXP (y facturas de CXP) como cerrados.
            13. Recalcular el saldo de las facturas.
            14. Recalcular el saldo de todos los clientes.
            15. Recalcular las existencias a hoy y establecer la fecha de corte (del cierre).
             */
            for (int i = 1; i <= 15; i++){
                switch (i){
                    case 1:setLblInfoText("Verificando integridad de facturas.."); break;
                    case 2:setLblInfoText("Verificando integridad de documentos de inventario.."); break;
                    case 3:setLblInfoText("Eliminando posibles inconsistencias.."); break;
                    case 4:setLblInfoText("Recalculando el inventario reservado.."); break;
                    case 5:setLblInfoText("Recalculando las cuentas por cobrar.."); break;
                    case 6:setLblInfoText("Recalculando saldo de clientes al cierre.."); break;
                    case 7:setLblInfoText("Recalculando inventarios al cierre.."); break;
                    case 8:setLblInfoText("Trasladando registros a los históricos.."); break;
                    case 9:setLblInfoText("Protegiendo facturas NC y ND en cuentas por cobrar.."); break;
                    case 10:setLblInfoText("Protegiendo registros de cuentas por cobrar en inventarios.."); break;
                    case 11:setLblInfoText("Protegiendo movimientos de inventarios.."); break;
                    case 12:setLblInfoText("Protegiendos recibos.."); break;
                    case 13:setLblInfoText("Recalculando facturas en cuentas por cobrar.."); break;
                    case 14:setLblInfoText("Recalculando saldos de clientes.."); break;
                    case 15:setLblInfoText("Recalculando inventarios a hoy.."); break;
                } // end switch
                cs.setInt(5, i);
                // Ejecutar el SP
                cs.executeUpdate();
                
                // Verificar si hubo error
                if (cs.getBoolean(3)){
                    error = true;
                    // Ejecutar un rollback
                    setLblInfoText("Se produjo un error..");
                    CMD.transaction(conn, CMD.ROLLBACK);
                    JOptionPane.showMessageDialog(
                            null,
                            cs.getString(4),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                } // end if
                this.progressBar.setValue(i);
            } // end for
            
            setLblInfoText("Confirmando datos finales..");
            
            cs.close();
            
            // Si no hay error confirmo todo el proceso
            if (!error){
                // Confirmar la transacción
                CMD.transaction(conn, CMD.COMMIT);
                setLblInfoText("¡Cierre terminado exitosamente!");
                JOptionPane.showMessageDialog(
                        null,
                        "Cierre mensual exitoso",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            } // end if
        } catch (SQLException ex) {
            if (hayTransaccion){
                // Ejecutar el rollback
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                    setLblInfoText("Se produjo un error..");
                } catch (SQLException ex1){
                    JOptionPane.showMessageDialog(null, 
                            ex1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage());
                }
            } // end if
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            JOptionPane.showMessageDialog(
                     null,
                     ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end catch
        this.close();
    } // end verificarConteo
    
    @Override
    public void run(){
        ejecutarCierreMensual();
    }
} // end ProgressBar
