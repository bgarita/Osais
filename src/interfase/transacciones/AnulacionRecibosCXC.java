/*
 * AnulacionRecibosCXC.java
 *
 * Created on 15/06/2010, 09:24:04 PM
 */

package interfase.transacciones;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.contabilidad.CoasientoE;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
@SuppressWarnings("serial")
public class AnulacionRecibosCXC extends java.awt.Dialog {
    private Connection conn;  // Conexión a la base de datos
    private Statement stat;
    String recibo;         // Aquí estará el recibo pasado por parámetro
    private ResultSet rs  = null;  // Uso general
    private final Bitacora b = new Bitacora();

    public AnulacionRecibosCXC(
            java.awt.Frame parent,
            boolean modal,
            Connection c, 
            String recnume) {
        
        super(parent, modal);
        initComponents();

        this.setAlwaysOnTop(false);

        conn   = c;
        recibo = recnume.trim();

        try {
            stat = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        // Si el número de recibo recibido es un cero entonces habilito
        // el campo para que el usuario pueda digitar un número.
        txtRecnume.setEnabled(Integer.parseInt(recibo) == 0);

        txtRecnume.setText(recibo);

        // Si el campo está habilitado le pongo el focus...
        if (txtRecnume.isEnabled()) {
            txtRecnume.requestFocusInWindow();
        } else { // ... caso contrario ejecuto el evento que busca el recibo
            txtRecnumeFocusLost(null);
            cmdAnular.requestFocusInWindow();
        } // end if
    } // end constructor

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdAnular = new javax.swing.JButton();
        cmdSalir = new javax.swing.JButton();
        txtRecnume = new javax.swing.JFormattedTextField();
        lblClidesc = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        txtMonto = new javax.swing.JTextField();
        lblMoneda = new javax.swing.JLabel();

        setIconImage(null);
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        setTitle("Anular pagos de clientes");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cmdAnular.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdAnular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZUNDO.png"))); // NOI18N
        cmdAnular.setText("Anular");
        cmdAnular.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdAnularMouseClicked(evt);
            }
        });
        cmdAnular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAnularActionPerformed(evt);
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

        txtRecnume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtRecnume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecnume.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRecnumeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRecnumeFocusLost(evt);
            }
        });
        txtRecnume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRecnumeActionPerformed(evt);
            }
        });

        lblClidesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblClidesc.setForeground(new java.awt.Color(0, 51, 255));
        lblClidesc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblClidesc.setText("  ");
        lblClidesc.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Documento #");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Fecha");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Monto");

        txtFecha.setEditable(false);
        txtFecha.setForeground(new java.awt.Color(204, 0, 204));

        txtMonto.setEditable(false);
        txtMonto.setForeground(new java.awt.Color(204, 0, 204));
        txtMonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lblMoneda.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMoneda.setForeground(new java.awt.Color(0, 51, 255));
        lblMoneda.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMoneda.setText("  ");
        lblMoneda.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblClidesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 89, Short.MAX_VALUE)
                        .addComponent(cmdAnular)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSalir))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAnular, cmdSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblClidesc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMoneda)
                .addGap(18, 42, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmdSalir)
                    .addComponent(cmdAnular))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAnular, cmdSalir});

        setSize(new java.awt.Dimension(325, 254));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void cmdAnularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnularActionPerformed
        this.cmdAnularMouseClicked(null);
    }//GEN-LAST:event_cmdAnularActionPerformed

    private void cmdSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSalirActionPerformed
        dispose();
    }//GEN-LAST:event_cmdSalirActionPerformed
/**
 * Búsqueda del recibo y despliegue del cliente
 * @param evt
 */
    private void txtRecnumeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRecnumeFocusLost
        recibo = txtRecnume.getText().trim();

        // Permito que el número sea cero o blanco para que el usuario
        // pueda usar otras opciones.
        if (recibo.equals("") || recibo.equals("0")){
            lblClidesc.setText("");
            txtFecha.setText("");
            txtMonto.setText("0.00");
            lblMoneda.setText("");
            return;
        } // end if
        
        // Consulto los datos del recibo.
        String sqlSelect =
                "Select                     " +
                "inclient.clidesc,          " +
                "Dtoc(pagos.fecha) as fecha," +
                "pagos.monto,               " +
                "monedas.descrip            " +
                "from pagos                 " +
                "Inner join inclient on pagos.clicode = inclient.clicode " +
                "Inner join monedas  on pagos.codigoTC = monedas.codigo  " +
                "Where recnume =   " + recibo + 
                " and estado = '' and cerrado = 'N'";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(
                    sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            //rs = UtilBD.SQLSelect(conn, sqlSelect);
            rs = CMD.select(ps);
        
            if (Ut.goRecord(rs, Ut.FIRST)){
                lblClidesc.setText(rs.getString("clidesc"));
                txtFecha.setText(rs.getString("Fecha"));
                
                    txtMonto.setText(
                                Ut.setDecimalFormat(
                                rs.getString("Monto"), "#,##0.00"));
                lblMoneda.setText(rs.getString("descrip"));
            }else{
                lblClidesc.setText("");
                txtFecha.setText("");
                txtMonto.setText("0.00");
                lblMoneda.setText("");
                JOptionPane.showMessageDialog(null,
                        "Recibo no encontrado." +
                        "\nPodría darse alguna de la siguientes " +
                        "situaciones:\n" +
                        "1. El recibo ya se encuentra anulado.\n" +
                        "2. El recibo se encuentra en un período cerrado.\n" +
                        "3. El recibo no existe en la base de datos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(AnulacionRecibosCXC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_txtRecnumeFocusLost

    private void txtRecnumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRecnumeActionPerformed
        // Esto provoca que se ejecute el FocusLost en txtRecnume
        txtRecnume.transferFocus();
    }//GEN-LAST:event_txtRecnumeActionPerformed

    private void txtRecnumeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRecnumeFocusGained
        txtRecnume.selectAll();
    }//GEN-LAST:event_txtRecnumeFocusGained

    private void cmdAnularMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdAnularMouseClicked
        if (!validarAccion()) {
            return;
        } // end if

        recibo = txtRecnume.getText().trim();

        // Confirmar la anulación
        int respuesta =
            JOptionPane.showConfirmDialog(null,
                    "¿Realmente desea anular este recibo?",
                    "Confirme..",
                    JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.NO_OPTION){
            return;
        } // end if

        boolean hayTransaccion = false;

        try {
            boolean genasienfac = false;
            // Verificar si hay interface contable
            String sqlSent = "Select genasienfac from config";
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (Ut.goRecord(rs, Ut.FIRST)) {
                genasienfac = rs.getBoolean("genasienfac");
            } // end if
            ps.close();
            
            hayTransaccion = CMD.transaction(conn, CMD.START_TRANSACTION);

            String sqlDelete = "Call AnularPagoCXC(" + recibo + ")";

            // Utilizo executeQuery() porque el SP devuelve un RS
            // ya sea para indicar el error o para indicar que todo
            // salió bien.
            rs = stat.executeQuery(sqlDelete);

            rs.first();

            if (rs.getBoolean(1)){
                JOptionPane.showMessageDialog(null,
                        rs.getString(2),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                CMD.transaction(conn, CMD.ROLLBACK);
                return;
            } // end if
            
            // Si hay interface contable...
            if (genasienfac) {
                // Obtener el número y tipo de asiento a anular
                sqlSent
                        = "Select no_comprob, tipo_comp "
                        + "From pagos "
                        + "Where recnume = " + recibo + " ";
                ps = conn.prepareStatement(sqlSent,
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = CMD.select(ps);
                Ut.goRecord(rs, Ut.FIRST);
                String no_comprob = rs.getString("no_comprob");
                short tipo_comp = rs.getShort("tipo_comp");
                ps.close();
                
                CoasientoE asientoE
                        = new CoasientoE(no_comprob, tipo_comp, conn);
                if (!asientoE.anular()) {
                    throw new SQLException(asientoE.getMensaje_error());
                } // end if
            } // end if

            CMD.transaction(conn, CMD.COMMIT);
            JOptionPane.showMessageDialog(null,
                    "Recibo anulado satisfactoriamente.",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);

            lblClidesc.setText("");
            txtFecha.setText("");
            txtMonto.setText("0.00");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            lblClidesc.setText("");
            txtFecha.setText("");
            txtMonto.setText("0.00");
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());

            if (hayTransaccion){
                try{
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1){
                    JOptionPane.showMessageDialog(null, 
                            ex1.getMessage(),
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                }
            } // end if
        }
    }//GEN-LAST:event_cmdAnularMouseClicked

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAnular;
    private javax.swing.JButton cmdSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblClidesc;
    private javax.swing.JLabel lblMoneda;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JFormattedTextField txtRecnume;
    // End of variables declaration//GEN-END:variables

    private boolean validarAccion(){
        boolean todoCorrecto = true;
        // Si la etiqueta que despliega el nombre del cliente está
        // vacía significa que el recibo digitado no es válido.
        if (this.lblClidesc.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,
                    "Número de recibo no válido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtRecnume.requestFocusInWindow();
            return false;
        } // end if

        return todoCorrecto;
    } // end validarAccion
}
