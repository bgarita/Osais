package logica.utilitarios;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window.Type;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 *
 * @author Bosco Garita Azofeifa 20/10/2020 Esta clase se usa para ejecutar
 * procesos largos en hilos separados, mostrando una barra de progreso.
 *
 */
public class ProcessBackground extends Thread {

    private final JFrame frame;
    private final Container content;
    private final JProgressBar progressBar;
    private Border border;
    private final javax.swing.JLabel lblInfo;

    private final Connection conn;
    
    private final String windowTitle;
    private final Bitacora b = new Bitacora();

    public ProcessBackground(Connection c, String windowTitle) {
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
        this.lblInfo.setPreferredSize(new Dimension(750, 18));
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

    public void setTitle(String title) {
        frame.setTitle(title);
    } // end setTitle

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    } // end setVisible

    public void setMaximumValue(int value) {
        progressBar.setMaximum(value);
    } // end setMaximumValue

    public void setValue(int value) {
        progressBar.setValue(value);
        progressBar.setStringPainted(true);
    } // end setValue

    public int getValue() {
        return this.progressBar.getValue();
    } // end getValue

    public final void setBorderTitle(String borderTitle) {
        border = BorderFactory.createTitledBorder(borderTitle);
        progressBar.setBorder(border);
    } // end setBorderTitle

    public void close() {
        setVisible(false);
        frame.dispose();
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                // No se hace nada con el error ya que no es importante.
            }
        } // end if
    } // end close

    @Override
    public void run() {
        // Modificar esta clase para utilizarla en varios procesos.
        // Acá se deberá usar un indicador para saber cuál proceso correr.
        try {
            String msg = cabys();
            JOptionPane.showMessageDialog(null,
                    msg,
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        close();
    } // end run

    private String cabys() throws IOException, FileNotFoundException, SQLException {
        this.setMaximumValue(100);
        this.setValue(0);
        this.setVisible(true);
        this.setLblInfoText("Cargando hoja de cálculo...");
        
        // Pendiente revisar esto porque sólo está mostrando hasta 76%
        return UtilBD.actualizarCabys(this.conn, this.progressBar, this.lblInfo);
    } // end cabys
} // end ProgressBar
