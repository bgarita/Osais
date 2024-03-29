package testing;

import Exceptions.EmptyDataSourceException;
import accesoDatos.CMD;
import logica.backup.BackupResoreJob;
import interfase.otros.BackupPassw;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JFileChooser; 
import javax.swing.JOptionPane;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class StartJob extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;
    private Properties prop, prop2;
    private InputStream input, input2;
    private FileOutputStream output, output2;
    private String sourceFile, targetFile;
    private Connection conn;
    private javax.swing.JPasswordField txtPassword;

    /**
     * Creates new form StartJob
     *
     * @param conn
     */
    public StartJob(Connection conn) {
        initComponents();
        this.txtPassword = new javax.swing.JPasswordField();
        this.conn = conn;
        sourceFile = "backupDB.properties";
        targetFile = "destinoF.properties";
        prop = new Properties();
        prop2 = new Properties();
        try {
            input = new FileInputStream(sourceFile);  // Lectura
            input2 = new FileInputStream(targetFile);  // Lectura
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartJob.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            File f = new File(sourceFile);
            System.out.println(f.getAbsolutePath());
            f = new File(targetFile);
            System.out.println(f.getAbsolutePath());
            dispose();
        } // end try-catch 
        
        try {
            /*
             Buscar en google http://www.mkyong.com/java/java-properties-file-examples/
             para crear los procesos de cargar el archivo de propiedades y para modificarlo.
             */
            loadProperties(); 
        } catch (IOException ex) {
            Logger.getLogger(StartJob.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        } // end try-catch

        loadDatabaseNames();
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
        tblConfig = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        txtProp = new javax.swing.JTextField();
        lblBDSeleccionada = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        lblDestino = new javax.swing.JLabel();
        txtDestino = new javax.swing.JTextField();
        btnResp = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        pgbAvance = new javax.swing.JProgressBar();
        lblBytes = new javax.swing.JLabel();
        cboBD = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Respaldo de base de datos");

        tblConfig.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nombre", "Base de datos a respaldar", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblConfig.getTableHeader().setReorderingAllowed(false);
        tblConfig.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblConfigMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblConfig);
        if (tblConfig.getColumnModel().getColumnCount() > 0) {
            tblConfig.getColumnModel().getColumn(0).setPreferredWidth(200);
            tblConfig.getColumnModel().getColumn(0).setMaxWidth(300);
            tblConfig.getColumnModel().getColumn(1).setPreferredWidth(290);
            tblConfig.getColumnModel().getColumn(1).setMaxWidth(350);
        }

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel1.setText("ID");

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setText("Base de datos");

        lblID.setForeground(new java.awt.Color(37, 0, 255));
        lblID.setText("0");

        lblBDSeleccionada.setForeground(new java.awt.Color(251, 10, 250));
        lblBDSeleccionada.setText(" ");

        btnAgregar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Add.png"))); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        lblDestino.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblDestino.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/CD.png"))); // NOI18N
        lblDestino.setText("Destino...");
        lblDestino.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDestinoMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblDestinoMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblDestinoMouseEntered(evt);
            }
        });

        txtDestino.setEditable(false);

        btnResp.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnResp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/DiscoExterno.png"))); // NOI18N
        btnResp.setText("Respaldar");
        btnResp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRespActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Turn off.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        btnGuardar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Save.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnEliminar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/No-entry.png"))); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        pgbAvance.setStringPainted(true);

        lblBytes.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblBytes.setForeground(java.awt.Color.red);

        cboBD.setToolTipText("Elija una base de datos");
        cboBD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblBytes, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(btnResp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)
                        .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                    .addComponent(pgbAvance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblDestino)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDestino))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(lblID))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtProp, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(lblBDSeleccionada, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboBD, 0, 238, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(124, 124, 124)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(cboBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregar)
                    .addComponent(btnEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblID)
                    .addComponent(txtProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBDSeleccionada))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDestino)
                    .addComponent(txtDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pgbAvance, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBytes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnResp)
                        .addComponent(btnSalir)
                        .addComponent(btnGuardar)))
                .addGap(4, 4, 4))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        if (this.lblBDSeleccionada.getText().trim().isEmpty()) { 
            JOptionPane.showMessageDialog(null,
                    "Primero debe elegir una base de datos en la lista desplegable.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        int ID = Integer.parseInt(lblID.getText().trim());

        // Si el ID es negativo se trata de una línea nueva y por tanto se agrega
        // una nueva línea.
        if (ID < 0) {
            int row = Ut.seekNull(tblConfig, 0);
            // Si esta función devuelve un -1 es porque no hay líneas vacías
            if (row < 0) {
                Ut.resizeTable(tblConfig, 1, "Filas");
                row = Ut.seekNull(tblConfig, 0);
            } // end if
            ID = row;
        } // end if

        tblConfig.setValueAt(this.txtProp.getText().trim(), ID, 0);
        tblConfig.setValueAt(lblBDSeleccionada.getText().trim(), ID, 1);
        this.lblID.setText(ID + "");
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void tblConfigMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblConfigMouseClicked
        if (tblConfig.getSelectedRow() < 0) {
            return;
        }

        lblID.setText(tblConfig.getSelectedRow() + "");
        txtProp.setText(
                tblConfig.getValueAt(tblConfig.getSelectedRow(), 0).toString());
        lblBDSeleccionada.setText(
                tblConfig.getValueAt(tblConfig.getSelectedRow(), 1).toString());
    }//GEN-LAST:event_tblConfigMouseClicked

    private void lblDestinoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDestinoMouseEntered
        lblDestino.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_lblDestinoMouseEntered

    private void lblDestinoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDestinoMouseExited
        lblDestino.setCursor(null);
    }//GEN-LAST:event_lblDestinoMouseExited

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        try {
            output = new FileOutputStream(sourceFile); // Escritura
            prop.clear();
            output2 = new FileOutputStream(targetFile); // Escritura
            prop2.clear();
            for (int i = 0; i < this.tblConfig.getModel().getRowCount(); i++) {
                if (tblConfig.getValueAt(i, 0) == null) {
                    continue;
                } // end if
                prop.setProperty(
                        tblConfig.getValueAt(i, 0).toString(),
                        tblConfig.getValueAt(i, 1).toString());
            } // end for
            prop.store(output, null);
            output.close();

            prop2.setProperty("backup_folder", this.txtDestino.getText().trim());
            prop2.store(output2, "Carpeta donde quedarán los respaldos");
            output2.close();
        } catch (Exception ex) {
            Logger.getLogger(StartJob.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch

        JOptionPane.showMessageDialog(null,
                "Configuración guardada satisfactoriamente.",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void lblDestinoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDestinoMouseClicked
        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int resultado = selectorArchivo.showOpenDialog(selectorArchivo);

        // Si el usuario hizo clic en el botón Cancelar del cuadro de diálogo, regresar
        if (resultado == JFileChooser.CANCEL_OPTION) {
            return;
        } // end if

        // obtener la carpeta seleccionada
        File carpeta = selectorArchivo.getSelectedFile();
        this.txtDestino.setText(carpeta.getAbsolutePath());
    }//GEN-LAST:event_lblDestinoMouseClicked

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (this.lblBDSeleccionada.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Primero debe elegir una fila de la tabla",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        int ID = Integer.parseInt(lblID.getText().trim());
        this.tblConfig.setValueAt(null, ID, 0);
        this.tblConfig.setValueAt(null, ID, 1);
        this.tblConfig.setValueAt(null, ID, 2);
        this.lblID.setText("-1");
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnRespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRespActionPerformed
        int rows = Ut.countNotNull(tblConfig, 0);
        if (rows == 0){
            JOptionPane.showMessageDialog(null, 
                    "No ha agregado bases de datos para respaldar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        
        // Esta es una ventana modal que pide la clave el usuario conectado (por seguridad).
        BackupPassw dialog = new BackupPassw(new javax.swing.JFrame(), true, this.txtPassword);
        dialog.setVisible(true);
        
        char[] aPass = txtPassword.getPassword();
        String password = "";
        for (char p : aPass){
            password += p;
        } // end for
        
        // Si la clave viene vacía no continúo
        if (password.isEmpty()){
            return;
        } // end if
        
        List<String> dataBases = new ArrayList<>();
        for (int i = 0; i < this.tblConfig.getModel().getRowCount(); i++){
            if (tblConfig.getValueAt(i, 1) == null || tblConfig.getValueAt(i, 1).toString().trim().isEmpty()){
                continue;
            } // end if
            dataBases.add(tblConfig.getValueAt(i, 1).toString().trim());
        } // end for

        BackupResoreJob bk = new BackupResoreJob();
        bk.setPassw(password);
        bk.setDataBases(dataBases);
        bk.setTargetFolder(this.txtDestino.getText().trim());
        bk.setLabel(this.lblBytes);
        bk.setPb(pgbAvance);
        bk.setTblConfig(tblConfig);
        bk.start();
    }//GEN-LAST:event_btnRespActionPerformed

    private void cboBDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBDActionPerformed
        int index = this.cboBD.getSelectedIndex();
        if (index < 0) {
            return;
        } // end if

        this.lblID.setText("-1");
        this.lblBDSeleccionada.setText("");
        this.txtProp.setText("");
        this.txtProp.requestFocusInWindow();

        this.lblBDSeleccionada.setText(this.cboBD.getSelectedItem().toString());
    }//GEN-LAST:event_cboBDActionPerformed

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(StartJob.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(StartJob.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(StartJob.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(StartJob.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new StartJob().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnResp;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox cboBD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBDSeleccionada;
    private javax.swing.JLabel lblBytes;
    private javax.swing.JLabel lblDestino;
    private javax.swing.JLabel lblID;
    private javax.swing.JProgressBar pgbAvance;
    private javax.swing.JTable tblConfig;
    private javax.swing.JTextField txtDestino;
    private javax.swing.JTextField txtProp;
    // End of variables declaration//GEN-END:variables

    private void loadProperties() throws IOException {
        prop.load(input);
        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = prop.getProperty(key);
            this.lblID.setText("-1");
            this.txtProp.setText(key);
            this.lblBDSeleccionada.setText(value);
            this.btnAgregarActionPerformed(null);
        } // end while

        this.lblID.setText("-1");
        this.txtProp.setText("");
        this.lblBDSeleccionada.setText("");

        prop2.load(input2);
        if (prop2.isEmpty()) {
            return;
        } // end if
        this.txtDestino.setText(prop2.getProperty("backup_folder"));

        input.close();
        input2.close();
    } // end loadProperties

    public void setPgbAdvance(int value) {
        this.pgbAvance.setValue(this.pgbAvance.getValue() + value);
    } // end setPgbAdvance

    private void loadDatabaseNames() {
        String sqlSent
                = "Select  "
                + "	SCHEMA_NAME  "
                + "from information_schema.SCHEMATA "
                + "order by 1";

        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);

            Ut.fillComboBox(cboBD, rs, 1, false);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (EmptyDataSourceException ex) {
            Logger.getLogger(StartJob.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch
    } // end loadDatabaseNames

    public void setConn(Connection conn) {
        this.conn = conn;
    }

}
