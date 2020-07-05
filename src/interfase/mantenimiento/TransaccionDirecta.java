package interfase.mantenimiento;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.Cacaja;
import logica.Catransa;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco 20/06/2015
 */
public class TransaccionDirecta extends javax.swing.JFrame {
    private static final long serialVersionUID = 5L;
    private final Catransa tran;
    private final Cacaja caja;
    private final Caja icaj;
    private final Connection conn;
    private final Bitacora b = new Bitacora();
    /**
     * Creates new form DepositoEfectivo
     * @param c
     * @param caja
     * @param icaj
     */
    public TransaccionDirecta(Connection c, Cacaja caja, Caja icaj) {
        initComponents();
        this.tran = new Catransa(c);
        this.caja = caja;
        this.icaj = icaj;
        this.conn = c;
        this.txtRecnume.setText(tran.getSiguienteRecibo() + "");
        
    } // end contructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        btnGuardar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtRecnume = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtMonto = new javax.swing.JFormattedTextField();
        radDep = new javax.swing.JRadioButton();
        radRet = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Depositar o retirar efectivo");

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Save.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Exit.png"))); // NOI18N
        btnSalir.setToolTipText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel3.setText("Consecutivo");

        txtRecnume.setEditable(false);
        txtRecnume.setForeground(java.awt.Color.red);
        txtRecnume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecnume.setText("0");
        txtRecnume.setFocusable(false);

        jLabel7.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel7.setText("Monto");

        txtMonto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMonto.setText("0.00");
        txtMonto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMontoFocusGained(evt);
            }
        });
        txtMonto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoActionPerformed(evt);
            }
        });

        buttonGroup1.add(radDep);
        radDep.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        radDep.setText("Depóstio");

        buttonGroup1.add(radRet);
        radRet.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        radRet.setText("Retiro");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7)
                            .addComponent(radRet))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                                .addComponent(btnGuardar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtRecnume, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtMonto)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(radDep)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(radDep)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnSalir, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radRet)))
                .addGap(4, 4, 4))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Desbloqueo la caja para poder registrar la transacción.
        // Esto no se debe hacer en ningún otro proceso, solo en este
        // porque esta pantalla solo la debe usar el usuario administrador.
        caja.bloquear(false);
        
        // Vuelvo a abrir la caja para garantizar que la transacción entra
        // en un tiempo válido.  Podría suceder que el mismo usuario tenga
        // abierta la pantalla de cajas y ejecute el cierre antes de venir a
        // esta pantalla e intentar guardar la transacción.
        caja.abrir(conn);

        if (caja.isError()){
            JOptionPane.showMessageDialog(null,
                caja.getMensaje_error(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Vuelvlo a bloquear la caja
        caja.bloquear(true);
        
        tran.closeAllRS();

        int tipopago;           // 0 = Desconocido, 1 = Efectivo, 2 = cheque, 3 = tarjeta, 4 = Transferencia
        boolean deposito;       // true, false
        double monto;           // Monto de la transacción
        boolean transac;        // Se usa para el control transaccional (true=hay transacción, false=No hay)
        int recnumeca;          // Número de recibo de caja
        Calendar cal;           // Se usa para obtener la fecha de hoy
        String sqlSent;         // Se usa para crear las sentencias SQL
        PreparedStatement ps;   // Sentencias SQL preparadas

        tipopago = 1;
        deposito = this.radDep.isSelected();

        
        try {
            monto = Double.parseDouble(Ut.quitarFormato(this.txtMonto.getText().trim()));
            if (monto < 0){
                JOptionPane.showMessageDialog(null, 
                        "El monto no puede ser negativo.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.txtMonto.requestFocusInWindow();
                return;
            } // end if

            tran.setMonto(monto);
        } catch (Exception ex) {
            Logger.getLogger(TransaccionDirecta.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch 

        // Confirmar el consecutivo
        recnumeca = tran.getSiguienteRecibo();
        tran.setRecnume(recnumeca);
        tran.setDocumento("");
        tran.setTipodoc("");
        tran.setTipomov(deposito ? "D":"R");

        cal = GregorianCalendar.getInstance();

        tran.setFecha(new Date(cal.getTimeInMillis()));
        tran.setCedula("");
        tran.setNombre(deposito ? "Depósito directo":"Retiro directo");
        tran.setTipopago(tipopago);
        tran.setReferencia("");
        tran.setIdcaja(caja.getIdcaja());
        tran.setCajero(caja.getUser());
        tran.setModulo("CAJ");
        tran.setIdbanco(0);

        tran.setIdtarjeta(0);

        transac = false;
        try {
            // Iniciar transacción
            CMD.transaction(conn, CMD.START_TRANSACTION);
            transac = true;

            // Actualizar la tabla de transacciones
            tran.registrar(deposito); // Hace el insert en catransa
            if (tran.isError()){
                CMD.transaction(conn, CMD.ROLLBACK);
                JOptionPane.showMessageDialog(null,
                    tran.getMensaje_error(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            // Actualizar el saldo de caja
            if (deposito){
                caja.setDepositos(caja.getDepositos() + tran.getMonto());
                caja.setSaldoactual(caja.getSaldoactual() + tran.getMonto());
                caja.setEfectivo(caja.getEfectivo() + monto);
            } else {
                caja.setRetiros(caja.getRetiros() + tran.getMonto());
                caja.setSaldoactual(caja.getSaldoactual() - tran.getMonto());
                caja.setEfectivo(caja.getEfectivo() - monto);
            } // end if else
            

            caja.actualizarTransacciones(); // Saldos y fechas

            if (caja.isError()){ // Si hay error... rollback
                CMD.transaction(conn, CMD.ROLLBACK);
                JOptionPane.showMessageDialog(null,
                    caja.getMensaje_error(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            // Actualizo el consecutivo de recibos de caja
            sqlSent = "Update config set recnumeca = ?";
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, tran.getRecnume());
            CMD.update(ps);

            // Si todo está bien confirmo la transacción
            CMD.transaction(conn, CMD.COMMIT);
            ps.close();
            
            icaj.refresh(); // Refrescar la caja
            
            JOptionPane.showMessageDialog(null,
                "Transacción registrada exitosamentge",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) { // Se mantiene por un tiempo para determinar distintos tipos de error 06/06/2015
            Logger.getLogger(TransaccionDirecta.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            if (transac){
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(TransaccionDirecta.class.getName()).log(Level.SEVERE, null, ex1);
                    JOptionPane.showMessageDialog(null,
                        "Ocurrió un error inesperado.\n" +
                        "El sistema se cerrará para proteger la integridad.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                    System.exit(1);
                } // end try-catch interno // end try-catch interno
            } // end if
        } // end try-catch 
        
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        tran.closeAllRS();
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void txtMontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoFocusGained
        txtMonto.selectAll();
    }//GEN-LAST:event_txtMontoFocusGained

    private void txtMontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoActionPerformed
        txtMonto.transferFocus();
    }//GEN-LAST:event_txtMontoActionPerformed

    /**
     * @param c
     * @param caja
     * @param caj
     */
    public static void main(final Connection c, final Cacaja caja, final Caja caj) {
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
            java.util.logging.Logger.getLogger(TransaccionDirecta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TransaccionDirecta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TransaccionDirecta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TransaccionDirecta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        try {
            if (!UtilBD.tienePermiso(c,"DepositoEfectivo")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado a realizar depósitos directos.",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            
        } catch (Exception ex) {
            Logger.getLogger(TransaccionDirecta.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch 
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TransaccionDirecta(c,caja,caj).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton radDep;
    private javax.swing.JRadioButton radRet;
    private javax.swing.JFormattedTextField txtMonto;
    private javax.swing.JTextField txtRecnume;
    // End of variables declaration//GEN-END:variables
}
