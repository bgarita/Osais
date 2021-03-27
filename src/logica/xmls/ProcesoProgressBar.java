package logica.xmls;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.consultas.DetalleNotificacionXml;
import interfase.menus.Menu;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import logica.utilitarios.Ut;

/**
 * Esta clase se encarga de cargar todos los registros 
 * de facturación electrónica según los criterios elegidos
 * por el usuario (dentro de un hilo separado).
 * @author Bosco Garita Azofeifa 27/03/2021 
 * 
 */
public class ProcesoProgressBar extends Thread {

    private final DetalleNotificacionXml dn;
    private final JFrame frame;
    private final Container content;
    private final JProgressBar progressBar;
    private Border border;
    private final Connection conn;
    private final String windowTitle;
    
    public ProcesoProgressBar(DetalleNotificacionXml dn, String windowTitle) {
        this.dn = dn;
        this.windowTitle = windowTitle;
        this.conn = Menu.CONEXION.getConnection();

        this.frame = new JFrame(windowTitle);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setType(Type.UTILITY);
        this.content = this.frame.getContentPane();
        this.progressBar = new JProgressBar();
        int height = Ut.getProperty(Ut.OS_NAME).equals("Linux") ? 90 : 85;
        int width = 400;
        this.frame.setSize(width, height);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        this.progressBar.setIndeterminate(true);
        frame.setLocationRelativeTo(null);
    } // end main

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

    public void setBorderTitle(String borderTitle) {
        border = BorderFactory.createTitledBorder(borderTitle);
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
    } // end setBorderTitle

    private void close() {
        frame.setVisible(false);
        frame.dispose();
    }


    @Override
    public void run() {
        loadData();
        this.close();
    } // end run
    
    private void loadData(){
        frame.setVisible(true);
        JTable table = dn.getTable();
        JLabel lblRecords = dn.getLblRecords();
        Bitacora b = dn.getBitacora();
        String query = dn.getQuery();
        Boolean init = dn.getInit();
        Boolean destroy = dn.getDestroy();
        
        init = true;
        destroy = false;
        
        // Limpiar la tabla.
        Ut.clearJTable(table);
        
        ResultSet rs;
        try {
            PreparedStatement ps = conn.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                ps.close();
                return;
            } // end if

            int rows, row;
            rs.last();
            rows = rs.getRow();
            try {
                lblRecords.setText("Total registros: " + Ut.setDecimalFormat(rows + "", 0));
            } catch (Exception ex) {
                lblRecords.setText("Total registros: N/A");
            }

            // Establecer el número de filas exacto
            DefaultTableModel dtm = (DefaultTableModel) table.getModel();
            dtm.setRowCount(rows);
            table.setModel(dtm);
            
            rs.beforeFirst();
            row = 0;
            while (rs.next()) {
                table.setValueAt(rs.getInt("facnume"), row, 0);
                table.setValueAt(rs.getString("tipo"), row, 1);
                table.setValueAt(rs.getString("xmlFile"), row, 2);
                table.setValueAt(rs.getString("descrip"), row, 3);
                table.setValueAt(rs.getString("clidesc"), row, 4);
                table.setValueAt(rs.getString("estado"), row, 5);
                table.setValueAt(rs.getString("xmlEnviado"), row, 6);
                table.setValueAt(rs.getString("emailDestino"), row, 7);
                table.setValueAt(rs.getString("fechaEnviado"), row, 8);
                table.setValueAt(rs.getInt("facnd"), row, 9);
                table.setValueAt(rs.getString("tipoxml"), row, 10);
                row++;
            } // end while
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
        init = false;
    } // end loadData
} // end ProgressBar
