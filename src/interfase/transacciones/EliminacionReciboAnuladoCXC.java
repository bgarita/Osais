/*
 * EliminacionReciboAnuladoCXC.java
 *
 * Created on 03/09/2011, 05:21:14 PM
 */

package interfase.transacciones;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Bosco
 */
@SuppressWarnings("serial")
public class EliminacionReciboAnuladoCXC extends javax.swing.JFrame {
    Connection conn;
    private final Bitacora b = new Bitacora();
    
    
    /** Creates new form EliminacionReciboAnuladoCXC
     * @param c */
    public EliminacionReciboAnuladoCXC(Connection c) {
        initComponents();
        conn = c;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        txtRecnume = new javax.swing.JFormattedTextField();
        txtClidesc = new javax.swing.JTextField();
        txtFecha = new javax.swing.JTextField();
        cmdBorrar = new javax.swing.JButton();
        cmdSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Eliminar recibos anulados (CXC)");

        jTextPane1.setEditable(false);
        jTextPane1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextPane1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextPane1.setForeground(new java.awt.Color(255, 0, 0));
        jTextPane1.setText("Esta opción elimina de la base de datos los recibos anulados con el fin de que se puedan volver a ingresar.  Es responsabilidad del usuario darle uso apropiado a esta herramienta.");
        jScrollPane1.setViewportView(jTextPane1);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Recibo:");

        txtRecnume.setColumns(6);
        txtRecnume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecnume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRecnumeActionPerformed(evt);
            }
        });
        txtRecnume.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRecnumeFocusGained(evt);
            }
        });

        txtClidesc.setEditable(false);
        txtClidesc.setForeground(new java.awt.Color(51, 0, 255));

        txtFecha.setEditable(false);
        txtFecha.setForeground(new java.awt.Color(51, 0, 255));

        cmdBorrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/eraser.png"))); // NOI18N
        cmdBorrar.setText("Borrar");
        cmdBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBorrarActionPerformed(evt);
            }
        });

        cmdSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        cmdSalir.setText("Cerrar");
        cmdSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtClidesc)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(cmdBorrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdSalir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdBorrar, cmdSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmdSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdBorrar))
                .addGap(5, 5, 5))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdBorrar, cmdSalir});

        setSize(new java.awt.Dimension(377, 276));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtRecnumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRecnumeActionPerformed
        this.txtClidesc.setText("");
        this.txtFecha.setText("");
        int recnume = 0;
        try {
            recnume = Integer.parseInt(txtRecnume.getText().trim());
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        // Consulto el registro.  Este recibo tiene que estar anulado ya que es
        // condición fundamental para la eliminación.
        String sqlSent =
            "Select " +
            "    dtoc(fecha) as fecha, clidesc " +
            "From pagos, inclient " +
            "Where recnume = ? " +
            "and estado = 'A'  " +
            "and pagos.clicode = inclient.clicode";

        PreparedStatement pr;
        ResultSet rs;

        try {
            pr = conn.prepareStatement(sqlSent);
            pr.setInt(1, recnume);
            rs = pr.executeQuery();
            if (rs == null || !rs.first()){
                JOptionPane.showMessageDialog(null,
                        "El recibo no existe o no está nulo.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            this.txtClidesc.setText(rs.getString("clidesc"));
            this.txtFecha.setText(rs.getString("fecha"));
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_txtRecnumeActionPerformed

    private void txtRecnumeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRecnumeFocusGained
        txtRecnume.selectAll();
}//GEN-LAST:event_txtRecnumeFocusGained

    private void cmdSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSalirActionPerformed
        dispose();
}//GEN-LAST:event_cmdSalirActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        if (txtClidesc.getText().trim().isEmpty() ||
                txtRecnume.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null,
                    "Debe digitar un recibo válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtRecnume.requestFocusInWindow();
        } // end if

        int recnume = 0;
        try {
            recnume = Integer.parseInt(txtRecnume.getText().trim());
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Confirmar la acción del usuario.
        int resp =
            JOptionPane.showConfirmDialog(null,
                    "¿Realmente desea borrar este recibo?",
                    "Confirme por favor..",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
        if (resp != JOptionPane.YES_OPTION){
            return;
        } // end if

        // Al llegar aquí ya se tiene seguridad de que el recibo será eliminado.

        // Hay que eliminar primero el detalle del recibo para evitar que la
        // integridad referencial de la base de datos cancele el borrado.
        String sqlDelete =
                "Delete from pagosd where recnume = ?";
        PreparedStatement pr;
        int registros;

        boolean hayTransaccion = false;

        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            hayTransaccion = true;
            pr = conn.prepareStatement(sqlDelete);
            pr.setInt(1, recnume);
            registros = pr.executeUpdate();

            // Mínimo un registro debe haber sido borrado.
            if (registros == 0){
                JOptionPane.showMessageDialog(null,
                        "No se pudo eliminar el detalle del recibo.\n" +
                        "Comuníquese con su Administrador de Base de Datos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if

            if (registros > 0){
                // Elimino el encabezado del recibo.
                sqlDelete = "Delete from pagos where recnume = ?";
                pr = conn.prepareStatement(sqlDelete);
                pr.setInt(1, recnume);
                registros = pr.executeUpdate();

                if (registros == 0){
                    JOptionPane.showMessageDialog(null,
                            "No se pudo eliminar el encabezado del recibo.\n" +
                            "Comuníquese con su Administrador de Base de Datos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // end if
            } // end if

            if (registros > 0){
                // Confirmo la transacción
                CMD.transaction(conn, CMD.COMMIT);
                JOptionPane.showMessageDialog(null,
                        "Recibo eliminado satisfactoriamente.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                // Pongo ahora todos los campos en blanco para que se vea
                // que algo sucedió.
                this.txtRecnume.setText("");
                this.txtClidesc.setText("");
                this.txtFecha.setText("");
            } else {
                // Descarto la transacción
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if-else

            hayTransaccion = false;
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            if (hayTransaccion){
                //UtilBD.SQLTransaction(conn, UtilBD.ROLLBACK);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1){
                    JOptionPane.showMessageDialog(null,
                        ex1.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }
            }
        } // end try-catch
    }//GEN-LAST:event_cmdBorrarActionPerformed

    /**
     * @param c
    */
    public static void main(final Connection c) {
        try {
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"EliminacionReciboAnuladoCXC")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(EliminacionReciboAnuladoCXC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EliminacionReciboAnuladoCXC(c).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JButton cmdSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextField txtClidesc;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JFormattedTextField txtRecnume;
    // End of variables declaration//GEN-END:variables

}
