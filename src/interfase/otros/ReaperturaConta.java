package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.contabilidad.CoactualizCat;
import logica.contabilidad.Coperiodoco;

/**
 *
 * @author bgarita
 */
public class ReaperturaConta extends javax.swing.JFrame {

    private static final long serialVersionUID = 19L;
    private Connection conn = null;

    private final Coperiodoco periodo;
    private final Bitacora b = new Bitacora();
    private boolean principio;
    private boolean fin;

    /**
     * Creates new form ReaperturaConta
     */
    public ReaperturaConta() {
        initComponents();
        conn = Menu.CONEXION.getConnection();

        principio = true;
        fin = false;

        periodo = new Coperiodoco(conn);
        periodo.cargarUltimoCerrado();
        this.datMes.setMonth(periodo.getMes());
        this.datAño.setYear(periodo.getAño() == 0 ? 2013 : periodo.getAño());
        principio = false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        paneAdvertencia = new javax.swing.JTextPane();
        datMes = new com.toedter.calendar.JMonthChooser();
        datAño = new com.toedter.calendar.JYearChooser();
        jLabel1 = new javax.swing.JLabel();
        btnEjecutar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reabrir periodos cerrados");

        paneAdvertencia.setEditable(false);
        paneAdvertencia.setText("PRECAUCIÓN:\nEste proceso abrirá el período contable que usted le indique pero debe tomar en cuenta los siguientes aspectos:\n1. Si abre un período que está varios meses atrás, también quedarán abiertos los períodos subsiguientes.\n2. Si los informes ya fueron entregados deberá volver a  generarlos y sustituir los anteriores.\n3. Es responsabilidad suya el uso que haga de esta opción ya podrá cambiar datos de períodos cerrados.\n4. Por seguridad, debe realizar un respaldo completo antes de ejecutar este proceso.");
        paneAdvertencia.setFocusable(false);
        jScrollPane1.setViewportView(paneAdvertencia);

        datMes.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datMesPropertyChange(evt);
            }
        });

        datAño.setStartYear(2000);
        datAño.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datAñoPropertyChange(evt);
            }
        });

        jLabel1.setText("Elija el mes y el año que desea reabrir:");

        btnEjecutar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnEjecutar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Ejecutar32.png"))); // NOI18N
        btnEjecutar.setToolTipText("Ejecutar");
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        btnSalir.setToolTipText("Cerrar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(103, 103, 103)
                                .addComponent(datMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(datAño, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1))
                        .addGap(0, 179, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalir)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEjecutar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(datMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datAño, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalir)
                    .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEjecutar, btnSalir});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void datMesPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datMesPropertyChange
        if (principio || fin) {
            return;
        }
        try {
            periodo.setMes(datMes.getMonth());
            periodo.setAño(datAño.getValue());
        } catch (Exception ex) {
            // No se hace nada, es solo para evitar que se caiga
        }
    }//GEN-LAST:event_datMesPropertyChange

    private void datAñoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datAñoPropertyChange
        datMesPropertyChange(evt);
    }//GEN-LAST:event_datAñoPropertyChange

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        int resp
                = JOptionPane.showConfirmDialog(null,
                        "Se dispone a realizar un proceso muy delicado.\n¿Realmente desea hacerlo?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.NO_OPTION) {
            return;
        } // end if

        int mes = datMes.getMonth() + 1;
        int año = datAño.getValue();

        /*
        1. Ejecutar el SP CALL ReabrirPeriodos([año],[mes])
            Este SP devuelve un rs con dos columnas err de tipo integer y msg de tipo String
            Si err es mayor que cero es porque se produjo un error y por lo tanto msg tendrá el mensaje del error.
            No se necesita hacer rollback porque el mismo SP controla la transacción.
        2. Si todo salió bien correr el proceso de recalcular cuentas de movimiento,
            y mayorizar (mismos procesos que se encuentran en el menú Herramientas).
        3. Mostrar el nuevo periodo actual.
        */
        
        String sqlSent =
                "CALL ReabrirPeriodos(?,?)";
        try {
            boolean error;
            String error_msg;
            try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {
                ps.setInt(1, año);
                ps.setInt(2, mes);
                ResultSet rs = CMD.select(ps);
                error = false;
                error_msg = "";
                if (rs != null && rs.first()){
                    error = rs.getInt(1) == 1;
                    error_msg = rs.getString(2);
                } // end if
            } // end try with resources
            
            if (error){
                throw new SQLException(error_msg);
            } // end if
            
            // Recalcular cuentas de movimientos
            CoactualizCat actuCat = new CoactualizCat(conn);
            error = !actuCat.recalcularSaldos();
            error_msg = actuCat.getMensaje_err();
            if (error){
                throw new SQLException(
                        "El período fue re-abierto pero ocurrió luego este error: " + error_msg);
            } // end if
            
            // Aplicar los asientos del periodo recien re-abierto
            error = !actuCat.sumarizarCuentas();
            error_msg = actuCat.getMensaje_err();
            if (error){
                throw new SQLException(
                        "El período fue re-abierto pero ocurrió luego este error: " + error_msg);
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch
        
        
        // Si el resultado del proceso es exitoso hay que establecer el nuevo
        // periodo contatable
        // Falta condicionarlo
        logica.contabilidad.PeriodoContable per = new logica.contabilidad.PeriodoContable(conn);
        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/Icons/calendar-day.png"));
        String msg = "El nuevo periodo contable en proceso es " + per.getMesLetras() + " " + per.getAño();

        JOptionPane.showMessageDialog(null,
                msg,
                "Periodo contable",
                JOptionPane.INFORMATION_MESSAGE,
                icon);
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        fin = true;
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(ReaperturaConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReaperturaConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReaperturaConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReaperturaConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ReaperturaConta().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnSalir;
    private com.toedter.calendar.JYearChooser datAño;
    private com.toedter.calendar.JMonthChooser datMes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane paneAdvertencia;
    // End of variables declaration//GEN-END:variables
}