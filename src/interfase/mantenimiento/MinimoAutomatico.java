
package interfase.mantenimiento;

import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class MinimoAutomatico extends javax.swing.JFrame {
    private static final long serialVersionUID = 3L;
    private Connection conn;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form MinimoAutomatico
     * @param c
     */
    public MinimoAutomatico(Connection c) {
        initComponents();
        
        conn = c;
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                btnCancelarActionPerformed(null);
            } // end windowClosing
        } // end class
        ); // end Listener
        
        // Cargar el combo de bodegas (codigo y descripción)
        String sqlSent = 
                "Select Concat(bodega, '-', descrip) as bodega from bodegas";
         PreparedStatement ps;
         ResultSet rs;
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs == null || !rs.first()){
                return;
            } // end if
            
            Ut.fillComboBox(cboBodega, rs, 1, false);
            ps.close();
        } catch (SQLException | EmptyDataSourceException ex) {
            Logger.getLogger(MinimoAutomatico.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboBodega = new javax.swing.JComboBox<String>();
        spnDiasProm = new javax.swing.JSpinner();
        spnDiasProc = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnProcesar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Generar mínimos de inventario");

        spnDiasProm.setModel(new javax.swing.SpinnerNumberModel(1, 1, 90, 1));

        spnDiasProc.setModel(new javax.swing.SpinnerNumberModel(30, 1, 1825, 1));

        jLabel1.setText("Para la bodega");

        jLabel2.setText("Generar mínimos para");

        jLabel3.setText("días");

        jLabel4.setText("Basándose en");

        jLabel5.setText("días de procesamiento");

        btnProcesar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnProcesar.setText("Procesar");
        btnProcesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(7, 8, 240));
        jLabel6.setText("Este proceso calcula el inventario mínimo para todos los productos");

        jLabel7.setForeground(new java.awt.Color(7, 8, 240));
        jLabel7.setText("basándose en estadísticas sobre ventas.");

        jLabel8.setForeground(new java.awt.Color(7, 8, 240));
        jLabel8.setText("Debe tomar en cuenta que si la duración del pedido es mayor al");

        jLabel9.setForeground(new java.awt.Color(7, 8, 240));
        jLabel9.setText("valor del primer campo de esta pantalla entonces se tomará ésta");

        jLabel10.setForeground(new java.awt.Color(7, 8, 240));
        jLabel10.setText("en vez de dicho valor.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboBodega, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(spnDiasProc)
                                    .addComponent(spnDiasProm))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(btnProcesar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancelar, btnProcesar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel6)
                .addGap(2, 2, 2)
                .addComponent(jLabel7)
                .addGap(2, 2, 2)
                .addComponent(jLabel8)
                .addGap(2, 2, 2)
                .addComponent(jLabel9)
                .addGap(2, 2, 2)
                .addComponent(jLabel10)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnDiasProm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnDiasProc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProcesar)
                    .addComponent(btnCancelar))
                .addGap(8, 8, 8))
        );

        setSize(new java.awt.Dimension(491, 330));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnProcesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesarActionPerformed
        // Ejecutar el SP Call GenerarMinimosPorBodega('001',8,500);
        String sqlSent; // Sentencia SQL
        String bodega;  // Bodega a procesar
        int promDias;   // Días a promediar
        int diasProc;   // Días a procesar de movimientos
        int registros;  // Registros procesados
        
        bodega = cboBodega.getSelectedItem().toString();
        bodega = bodega.substring(0, Ut.getPosicion(bodega, "-"));
        promDias = Integer.parseInt(spnDiasProm.getValue().toString());
        diasProc = Integer.parseInt(this.spnDiasProc.getValue().toString());
        
        PreparedStatement ps;
        sqlSent = "Call GenerarMinimosPorBodega(?,?,?)";
        try {
            // No hago control de transacciones porque no importa si da error,
            // se puede volver a correr.
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, bodega);
            ps.setInt(2, promDias);
            ps.setInt(3, diasProc);
            ResultSet rs  = CMD.select(ps);
            rs.first();
            registros = rs.getInt(1);
            
            JOptionPane.showMessageDialog(null, 
                    "Se actualizaron " + registros + " registros \n" +
                    "para la bodega " + cboBodega.getSelectedItem(),
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(MinimoAutomatico.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
    }//GEN-LAST:event_btnProcesarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MinimoAutomatico.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    /**
     * @param c
     */
    public static void main(final Connection c) {
        //Connection c = DataBaseConnection.getConnection("temp");
        try {
            if (!UtilBD.tienePermiso(c,"MinimoAutomatico")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            //DataBaseConnection.setFreeConnection("temp");
        } catch (Exception ex) {
            Logger.getLogger(MinimoAutomatico.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MinimoAutomatico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MinimoAutomatico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MinimoAutomatico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MinimoAutomatico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MinimoAutomatico(c).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnProcesar;
    private javax.swing.JComboBox<String> cboBodega;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSpinner spnDiasProc;
    private javax.swing.JSpinner spnDiasProm;
    // End of variables declaration//GEN-END:variables
}
