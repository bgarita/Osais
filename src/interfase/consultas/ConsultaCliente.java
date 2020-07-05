/*
 * ConsultaCliente.java
 *
 * Created on 01/11/2011 07:30 PM by Bosco Garita
 * Consultar clientes
 * 
 */
package interfase.consultas;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
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
 * @author Bosco Garita
 */
public class ConsultaCliente extends javax.swing.JFrame {

    private static final long serialVersionUID = 15L;
    private final Connection conn;
    private final String sqlSent;
    private final PreparedStatement pr;

    /**
     * Creates new form JFrame1
     *
     * @param c
     * @throws java.sql.SQLException
     */
    public ConsultaCliente(Connection c) throws SQLException {
        initComponents();

        conn = c;
        sqlSent
                = "Select        "
                + "    clidesc,  "
                + "    clicelu,  "
                + "    clitel1,  "
                + "    clitel2,  "
                + "    clidir,   "
                + "    direncom, "
                + "    cliemail, "
                + "    clisald   "
                + "from inclient "
                + "Where clicode = ?";
        pr = conn.prepareStatement(sqlSent);
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFamilia = new javax.swing.JLabel();
        txtClicode = new javax.swing.JFormattedTextField();
        txtClicelu = new javax.swing.JTextField();
        txtClitel1 = new javax.swing.JTextField();
        txtClitel2 = new javax.swing.JTextField();
        txtClidesc = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaClidir = new javax.swing.JTextArea();
        txtCliemail = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaDirencom = new javax.swing.JTextArea();
        lblFamilia1 = new javax.swing.JLabel();
        lblFamilia2 = new javax.swing.JLabel();
        lblFamilia3 = new javax.swing.JLabel();
        lblFamilia4 = new javax.swing.JLabel();
        txtClisald = new javax.swing.JTextField();
        btnSalir = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuSalir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Consultar clientes");
        setLocationByPlatform(true);

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Cliente");

        txtClicode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClicode.setToolTipText("Código de cliente");
        txtClicode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClicodeActionPerformed(evt);
            }
        });
        txtClicode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClicodeFocusLost(evt);
            }
        });

        txtClicelu.setEditable(false);
        txtClicelu.setForeground(java.awt.Color.blue);

        txtClitel1.setEditable(false);
        txtClitel1.setForeground(java.awt.Color.blue);

        txtClitel2.setEditable(false);
        txtClitel2.setForeground(java.awt.Color.blue);

        txtClidesc.setEditable(false);
        txtClidesc.setForeground(java.awt.Color.blue);

        txaClidir.setEditable(false);
        txaClidir.setColumns(20);
        txaClidir.setForeground(java.awt.Color.blue);
        txaClidir.setLineWrap(true);
        txaClidir.setRows(5);
        txaClidir.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaClidir);

        txtCliemail.setEditable(false);
        txtCliemail.setForeground(java.awt.Color.blue);

        txaDirencom.setColumns(20);
        txaDirencom.setEditable(false);
        txaDirencom.setForeground(java.awt.Color.blue);
        txaDirencom.setLineWrap(true);
        txaDirencom.setRows(5);
        txaDirencom.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txaDirencom);

        lblFamilia1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia1.setText("E-mail");

        lblFamilia2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia2.setText("Dir.");

        lblFamilia3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia3.setText("Encom.");

        lblFamilia4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblFamilia4.setText("Saldo");

        txtClisald.setEditable(false);
        txtClisald.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtClisald.setForeground(java.awt.Color.blue);
        txtClisald.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jMenu1.setText("Archivo");

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSalir);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edición");

        mnuBuscar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        mnuBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/binocular.png"))); // NOI18N
        mnuBuscar.setText("Buscar");
        mnuBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBuscarActionPerformed(evt);
            }
        });
        jMenu2.add(mnuBuscar);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFamilia)
                    .addComponent(lblFamilia1)
                    .addComponent(lblFamilia2)
                    .addComponent(lblFamilia3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblFamilia4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtClidesc, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtClicelu, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtClitel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtClitel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtCliemail, javax.swing.GroupLayout.Alignment.LEADING)))
                .addGap(50, 50, 50))
            .addGroup(layout.createSequentialGroup()
                .addGap(179, 179, 179)
                .addComponent(btnSalir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtClicelu, txtClitel1, txtClitel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFamilia)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClicelu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClitel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClitel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFamilia1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(lblFamilia2)
                        .addGap(73, 73, 73)
                        .addComponent(lblFamilia3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFamilia4))
                .addGap(18, 18, 18)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(432, 420));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtClicodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicodeActionPerformed
        cargarCliente();
}//GEN-LAST:event_txtClicodeActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        Buscador bd
                = new Buscador(
                        new java.awt.Frame(),
                        true,
                        "inclient", "clicode,clidesc", "clidesc", txtClicode, conn);
        bd.setVisible(true);
        txtClicodeActionPerformed(null);
    }//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtClicodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusLost
        txtClicode.setText(txtClicode.getText().toUpperCase());
    }//GEN-LAST:event_txtClicodeFocusLost

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    /**
     * @param c
     */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "Inclient2")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(ConsultaCliente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            ConsultaCliente run = new ConsultaCliente(c);
            run.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalir;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblFamilia;
    private javax.swing.JLabel lblFamilia1;
    private javax.swing.JLabel lblFamilia2;
    private javax.swing.JLabel lblFamilia3;
    private javax.swing.JLabel lblFamilia4;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTextArea txaClidir;
    private javax.swing.JTextArea txaDirencom;
    private javax.swing.JTextField txtClicelu;
    private javax.swing.JFormattedTextField txtClicode;
    private javax.swing.JTextField txtClidesc;
    private javax.swing.JTextField txtCliemail;
    private javax.swing.JTextField txtClisald;
    private javax.swing.JTextField txtClitel1;
    private javax.swing.JTextField txtClitel2;
    // End of variables declaration//GEN-END:variables

    public void cargarCliente() {
        if (txtClicode.getText().trim().equals("0")) {
            return;
        } // end if
        try {
            int clicode = Integer.parseInt(txtClicode.getText().trim());
            pr.setInt(1, clicode);
            ResultSet rs;
            rs = CMD.select(pr);

            if (rs == null || !rs.first()) {
                JOptionPane.showMessageDialog(null,
                        "Cliente no existe. Pruebe el buscador (CTRL+B).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            txtClicelu.setText(rs.getString("clicelu"));
            txtClitel1.setText(rs.getString("clitel1"));
            txtClitel2.setText(rs.getString("clitel2"));
            txtClidesc.setText(rs.getString("clidesc"));
            txtCliemail.setText(rs.getString("cliemail"));
            txaClidir.setText(rs.getString("clidir"));
            txaDirencom.setText(rs.getString("direncom"));
            txtClisald.setText(Ut.setDecimalFormat(rs.getString("clisald"), "#,##0.00"));
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Bitacora b = new Bitacora();
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarCliente
} // end class
