package contabilidad.view;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.menus.Menu;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import contabilidad.model.PeriodoContable;

/**
 *
 * @author bgari
 */
public class CierreConta extends javax.swing.JFrame {

    private static final long serialVersionUID = 20L;
    private final Bitacora b = new Bitacora();
    private final Connection conn;
    private final PeriodoContable per;

    /**
     * Creates new form CierreConta
     */
    public CierreConta() {
        initComponents();
        this.conn = Menu.CONEXION.getConnection();
        this.per = new PeriodoContable(conn);
        this.lblPeriodo.setText("Periodo a cerrar: " + per.getMesLetras() + ", " + per.getAño());
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        btnEjecutar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        lblPeriodo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cierre contable");

        jTextPane1.setEditable(false);
        jTextPane1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextPane1.setText("Este proceso trasladará todos los movimientos del periodo actual a las tablas de periodos cerrados y marcará como cerrado el periodo actual de tal manera que ya no será posible modificar ni agregar más datos al periodo en curso.\n\nAsegúrese de:\n1.  Realizar un buen respaldo de la base de datos.\n2.  Que no haya más usuarios en el sistema.");
        jScrollPane1.setViewportView(jTextPane1);

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

        lblPeriodo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPeriodo.setForeground(new java.awt.Color(51, 51, 255));
        lblPeriodo.setText("Periodo a cerrar:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalir)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(lblPeriodo)
                .addContainerGap(214, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEjecutar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(lblPeriodo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalir)
                    .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEjecutar, btnSalir});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        int resp
                = JOptionPane.showConfirmDialog(null,
                        "Se dispone a realizar el cierre mensual de contabilidad.\n¿Está seguro?",
                        "Confirme por favor",
                        JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.NO_OPTION) {
            return;
        } // end if

        try {
            // Valida que no haya movimientos desbalanceados
            b.setLogLevel(Bitacora.INFO);
            b.writeToLog(this.getClass().getName() + "--> Buscando asientos desbalanceados...", Bitacora.INFO);
            if (!UtilBD.CGestaBalanceado(conn)) {
                JOptionPane.showMessageDialog(null,
                        "Existen movimientos desbalanceados.\n"
                        + "Debe corregirlos para poder continuar.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            // 1. Guardar una copia del catálogo de cuentas tal y como está al momento del cierre
            // 2. Mover los asientos del periodo actual a la tabla histórica
            // 3. Cerrar el periodo actual
            // 4. Establecer el nuevo periodo
            CMD.transaction(conn, CMD.START_TRANSACTION);

            b.writeToLog(this.getClass().getName() + "--> Guardando copia del catálogo...", Bitacora.INFO);
            
            // Guardar una copia del catálogo y establece los saldos iniciales para el nuevo periodo.
            boolean correcto = UtilBD.CGguardarCatalogo(conn, per.getFecha_fi());

            if (correcto) {
                b.writeToLog(this.getClass().getName() + "--> Moviendo cuentas y asientos a periodos cerrados...", Bitacora.INFO);
                correcto = UtilBD.CGmoverAsientosHistorico(conn);
            }

            if (correcto) {
                b.writeToLog(this.getClass().getName() + "--> Estableciendo nuevo periodo...", Bitacora.INFO);
                // Se deja el periodo actual como cerrado y se configuran los parámetros del nuevo periodo.
                correcto = UtilBD.CGcerrarPeriodoActual(conn, per);
            } // end if

            if (correcto) {
                CMD.transaction(conn, CMD.COMMIT);
                JOptionPane.showMessageDialog(null,
                        "Cierre contable completado exitosamente!",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> Cierre contable completado satisfactoriamente.", Bitacora.INFO);
                this.dispose();
            } else {
                CMD.transaction(conn, CMD.ROLLBACK);
                JOptionPane.showMessageDialog(null,
                        "El cierre contable no se pudo realizar."
                        + "\nPuede revisar la bitácora de errores para ver la causa del error"
                        + "\no contacte al administrador del sistema",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if-else
        } catch (SQLException ex) {
            b.setLogLevel(Bitacora.ERROR);
            // Si se produjo un error y ya se había iniciado la transacción...
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(null,
                        "Se produjo un error a nivel de motor de base de datos.\n"
                        + "El sistema se cerrará para proteger la integridad.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage() + " Error en el motor.  Se cierra el sistema", Bitacora.INFO);
                System.exit(-1);
            }

            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
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
            java.util.logging.Logger.getLogger(CierreConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CierreConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CierreConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CierreConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CierreConta().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel lblPeriodo;
    // End of variables declaration//GEN-END:variables
}
