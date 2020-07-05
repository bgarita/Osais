/*
 * CierreMensual.java
 *
 * Created on 16/02/2011, 09:24:00 PM
 */
package interfase.transacciones;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import java.awt.Cursor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import logica.utilitarios.ProcessProgressBar;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class CierreMensual extends JFrame {

    private Connection conn = null;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form
     *
     * @param c
     * @throws java.sql.SQLException
     */
    public CierreMensual(Connection c) throws SQLException {
        initComponents();
        conn = c;
        txtAno.setText("" + GregorianCalendar.getInstance().get(Calendar.YEAR));

    } // end constructor

    public void setConexion(Connection c) {
        conn = c;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cboMes = new javax.swing.JComboBox();
        txtAno = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cierre mensual");

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardar))
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGuardar, btnSalir});

        jTextArea1.setBackground(new java.awt.Color(255, 255, 204));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextArea1.setForeground(java.awt.Color.blue);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Este proceso guarda una imagen del estado de las tablas principales (Artículos, Existencias, Clientes, Proveedores) y establece como cerradas todas las bodegas cuya fecha sea inferior al último día del mes que elija.  Esta fecha también será establecida como regla general para que no se puedan realizar cambios a las transacciones cuya fecha sea inferior a ella.");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextArea1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextArea1.setDisabledTextColor(java.awt.Color.blue);
        jTextArea1.setEnabled(false);
        jTextArea1.setFocusable(false);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 51));
        jLabel1.setText("IMPORTANTE!");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setText("Recuerde hacer un respaldo completo antes de ejecutar el cierre.");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Mes a cerrar:");

        cboMes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboMes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre" }));

        txtAno.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtAno.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Año:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAno, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mnuArchivo.setText("Archivo");

        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.JPG"))); // NOI18N
        mnuGuardar.setText("Ejecutar cierre");
        mnuGuardar.setPreferredSize(new java.awt.Dimension(200, 25));
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
        mnuSalir.setPreferredSize(new java.awt.Dimension(150, 21));
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuSalir);

        jMenuBar1.add(mnuArchivo);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        int resp
                = JOptionPane.showConfirmDialog(null,
                        "¿Realmente desea ejecutar el cierre?",
                        "Confirme..",
                        JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        } // end if

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        int mes = cboMes.getSelectedIndex() + 1;
        int ano = Integer.parseInt(this.txtAno.getText());
        String sqlCall = "Call EjecutarCierreMensual(?,?,?,?,?)";

        try {
            CallableStatement cs = conn.prepareCall(sqlCall);

            // Parámetros: conexión, título.
            ProcessProgressBar pp = new ProcessProgressBar(conn, "Cierre mensual");
            pp.setAno(ano);
            pp.setMes(mes);
            pp.setMaximumValue(12);
            pp.setCs(cs);
            pp.setBorderTitle("Procesando...");
            pp.setVisible(true);
            pp.start();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end catch
        this.setCursor(null);
        this.dispose();
}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
}//GEN-LAST:event_btnSalirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    /**
     * @param sC
     * @
     */
    public static void main(final Connection sC) {
        //final Connection c = DataBaseConnection.getConnection("temp");
        try {
            //JFrame.setDefaultLookAndFeelDecorated(true);
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(sC, "EjecutacionCierreMensual")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(CierreMensual.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch

        try {
            CierreMensual run = new CierreMensual(sC);
            run.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox cboMes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtAno;
    // End of variables declaration//GEN-END:variables

    /**
     * Este método hace un llamado al SP ConsultarDocumento para determinar si
     * el Movdocu digitado ya existe o no.
     *
     * @param Movdocu Movdocu a validar contra el encabezado de documentos de
     * inventario.
     * @return true = el Movdocu ya existe, false = el Movdocu no existe.
     */
    private boolean ExisteDocumento(String Movdocu) {
        Movdocu = "'" + Movdocu.trim() + "'";
        String Movtimo = "'E'";
        String Movtido = "11"; // Ajuste por entrada

        // Se usa como default.  Si ocurrierra un error esto no permitiría
        // el ingreso de ese documento.
        boolean existe = true;

        try {
            Statement st = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rsDoc;
            String sqlQuery
                    = "Select ConsultarDocumento("
                    + Movdocu + ","
                    + Movtimo + ","
                    + Movtido + ")";

            rsDoc = st.executeQuery(sqlQuery);
            rsDoc.first();

            existe = rsDoc.getBoolean(1);

            // Si el documento no existe como entrada trato de ver
            // si existe como salida.
            if (!existe) {
                Movtimo = "'S'";
                Movtido = "12"; // Ajuste por salida
                sqlQuery
                        = "Select ConsultarDocumento("
                        + Movdocu + ","
                        + Movtimo + ","
                        + Movtido + ")";

                rsDoc = st.executeQuery(sqlQuery);
                rsDoc.first();
                existe = rsDoc.getBoolean(1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end catch
        return existe;
    } // ExisteDocumento

} // end class
