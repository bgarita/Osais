package interfase.otros;

import Mail.Bitacora;
import Mail.MailSender;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.menus.Menu;
import static interfase.menus.Menu.DIR;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.DocumentoElectronico;
import logica.utilitarios.DirectoryStructure;
import logica.utilitarios.Ut;
import logica.xmls.Clave;
import logica.xmls.CodigoTipoMoneda;
import logica.xmls.DetalleFactura;
import logica.xmls.DetalleNotaCredito;
import logica.xmls.Emisor;
import logica.xmls.FacturaElectronica;
import logica.xmls.FacturaElectronicaCompra;
import logica.xmls.GeneraXML;
import logica.xmls.InformacionReferencia;
import logica.xmls.NotaCreditoElectronica;
import logica.xmls.NotaDebitoElectronica;
import logica.xmls.Otros;
import logica.xmls.Receptor;
import logica.xmls.ResumenFactura;
import logica.xmls.ResumenNotaCredito;
import logica.xmls.TiqueteElectronico;

/**
 *
 * @author bosco
 */
public class FacturaXML extends javax.swing.JFrame {

    private static final long serialVersionUID = 201L;
    public static String claveDocumento;
    private final Connection conn;
    private String sucursal;
    private String terminal;
    private String tipoComprobante;
    private int situacionComprobante;
    private final Bitacora b = new Bitacora();

    // Forma de ejecusión 
    public static final int INTERACTIVE = 1;
    public static final int UNATTENDED = 2;
    private int mode;

    // Tipo de documento electrónico
    public static final int FACTURA = 1;
    public static final int NOTACR = 2;
    public static final int NOTADB = 3;
    public static final int TIQUETE = 4;

    // Bitácora de envíos
    private List<String> bitacora;

    /**
     * Creates new form FacturaXML
     *
     * @param conn
     */
    public FacturaXML(Connection conn) {
        try {
            initComponents();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        this.conn = conn;
    }

    private void actualizarConteoEnvios() throws SQLException {
        String sqlUpdateExpression = "cantidad = cantidad + 1";
        String where = "";
        int records = UtilBD.updateTable(conn, "fecontrol", sqlUpdateExpression, where);

        // Este mensaje no es para el usuario, solo es para quien revise el log.
        if (records != 1) {
            String msg
                    = "El número de registro actualizados en el control de\n"
                    + "documentos electrónicos enviados es in correcto.\n"
                    + "Debió actulizarse 1 y se actualizaron " + records + ".";
            b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.ERROR);
        }
    }

    private boolean envioAgotado() throws Exception {
        boolean agotado = false;
        String tabla = "fecontrol";
        String permitidos = UtilBD.getDBString(conn, tabla, "permitidos <> 0", "permitidos");
        if (permitidos.isEmpty()) {
            throw new Exception("El control de envío de documentos no está configurado.");
        }

        if (Integer.parseInt(permitidos) == -1) { // Sin límite
            return agotado;
        }

        int cantidad = Integer.parseInt(UtilBD.getDBString(conn, tabla, "cantidad >= 0", "cantidad"));
        int tolerancia = Integer.parseInt(UtilBD.getDBString(conn, tabla, "cantidad >= 0", "tolerancia"));

        int leQuedan = Integer.parseInt(permitidos) + tolerancia - cantidad;

        if (leQuedan <= 0) {
            // El envío de correo se pone antes que el mensaje porque tarda mucho más
            // y el usuario tiende a confundirse pensando que ya terminó.

            // Enviar mensaje por correo
            String msg = "El número de documentos electrónicos a enviar ya fue agotado.";
            MailSender envioCorreo = new MailSender();
            envioCorreo.initGMail();
            msg = "<H1>" + Menu.EMPRESA + "</H1><h4>" + msg + "</h4>";
            envioCorreo.sendHTMLMail("bgarita@hotmail.com", "Facturación electrónica", msg);

            JOptionPane.showMessageDialog(null,
                    msg,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.ERROR);
            agotado = true;
        } // end if

        if (cantidad >= Integer.parseInt(permitidos) && leQuedan > 0) {
            String msg
                    = "El número permitido de envío de documentos electrónicos\n"
                    + "ya fue alcanzado.\n"
                    + "Se le permitirá enviar " + leQuedan + " más.\n"
                    + "En cuanto se agote ese número, no podrá enviar más.";
            JOptionPane.showMessageDialog(null,
                    msg,
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        } // end if

        return agotado;
    } // end envioAgotado

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        txtFacnume1 = new javax.swing.JFormattedTextField();
        txtFacnume2 = new javax.swing.JFormattedTextField();
        radFactura = new javax.swing.JRadioButton();
        radNC = new javax.swing.JRadioButton();
        radND = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaBitacora = new javax.swing.JTextArea();
        btnSalir = new javax.swing.JButton();
        cboSituacionComprobante = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        radFactCompra = new javax.swing.JRadioButton();
        radTiquete = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generar el XML de factura electrónica");

        txtFacnume1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtFacnume1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacnume1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFacnume1FocusLost(evt);
            }
        });
        txtFacnume1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFacnume1ActionPerformed(evt);
            }
        });

        txtFacnume2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtFacnume2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacnume2FocusGained(evt);
            }
        });

        buttonGroup1.add(radFactura);
        radFactura.setSelected(true);
        radFactura.setText("Factura");
        radFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radFacturaActionPerformed(evt);
            }
        });

        buttonGroup1.add(radNC);
        radNC.setText("Nota C");
        radNC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNCActionPerformed(evt);
            }
        });

        buttonGroup1.add(radND);
        radND.setText("Nota D");
        radND.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNDActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel1.setText("De");

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel3.setText("A");

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        txaBitacora.setEditable(false);
        txaBitacora.setColumns(20);
        txaBitacora.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txaBitacora.setLineWrap(true);
        txaBitacora.setRows(8);
        txaBitacora.setTabSize(5);
        txaBitacora.setWrapStyleWord(true);
        txaBitacora.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bitácora", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane1.setViewportView(txaBitacora);

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        btnSalir.setToolTipText("Cerrar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        cboSituacionComprobante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Contingencia", "Sin Internet" }));

        jLabel2.setText("Situación");

        buttonGroup1.add(radFactCompra);
        radFactCompra.setText("F Compra");
        radFactCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radFactCompraActionPerformed(evt);
            }
        });

        buttonGroup1.add(radTiquete);
        radTiquete.setText("Tiquete");
        radTiquete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radTiqueteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radFactura)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(radND)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(radNC))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radTiquete)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(radFactCompra)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtFacnume1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtFacnume2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboSituacionComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalir))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFacnume1, txtFacnume2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radFactura)
                    .addComponent(jLabel1)
                    .addComponent(txtFacnume1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radND)
                    .addComponent(radNC))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtFacnume2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(radTiquete)
                            .addComponent(radFactCompra))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboSituacionComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalir)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGuardar, btnSalir});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.bitacora = new ArrayList<>();
        this.txaBitacora.setText("");
        generateByDocument();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        try {
            if (this.mode == FacturaXML.INTERACTIVE) {
                this.conn.close();
            } // end if

        } catch (SQLException ex) {
            Logger.getLogger(FacturaXML.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void txtFacnume1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFacnume1ActionPerformed
        txtFacnume1.transferFocus();
    }//GEN-LAST:event_txtFacnume1ActionPerformed

    private void txtFacnume1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacnume1FocusGained
        txtFacnume1.selectAll();
    }//GEN-LAST:event_txtFacnume1FocusGained

    private void txtFacnume2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacnume2FocusGained
        txtFacnume2.selectAll();
    }//GEN-LAST:event_txtFacnume2FocusGained

    private void radFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radFacturaActionPerformed
        if (radFactura.isSelected()) {
            this.tipoComprobante = "01";
        }
    }//GEN-LAST:event_radFacturaActionPerformed

    private void radNDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNDActionPerformed
        if (radND.isSelected()) {
            // En la nueva versión 4.3 no están aceptando este tipo de documento
            // por lo que se envía como si fuera una factura.
            this.tipoComprobante = "02";
            //this.tipoComprobante = "01";
        }
    }//GEN-LAST:event_radNDActionPerformed

    private void radNCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNCActionPerformed
        if (radNC.isSelected()) {
            this.tipoComprobante = "03";
        }
    }//GEN-LAST:event_radNCActionPerformed

    private void txtFacnume1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacnume1FocusLost
        // Si el número es negativo paso automáticamente a nota de crédito
        int facnume = Integer.parseInt(this.txtFacnume1.getText().trim());
        if (facnume < 0) {
            this.radNC.setSelected(true);
            this.tipoComprobante = "03";
        } // end if

        if (this.mode == INTERACTIVE) {
            this.txtFacnume2.setText(facnume + "");
            this.txtFacnume2.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtFacnume1FocusLost

    private void radFactCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radFactCompraActionPerformed
        if (radFactCompra.isSelected()) {
            this.tipoComprobante = "08";
        }
    }//GEN-LAST:event_radFactCompraActionPerformed

    private void radTiqueteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radTiqueteActionPerformed
        if (radTiquete.isSelected()) {
            this.tipoComprobante = "04";
        }
    }//GEN-LAST:event_radTiqueteActionPerformed

    /**
     * @param c
     */
    public static void main(final Connection c) {
        try {
            if (!UtilBD.tienePermiso(c, "FacturaXML")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(FacturaXML.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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
            java.util.logging.Logger.getLogger(FacturaXML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FacturaXML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FacturaXML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FacturaXML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new FacturaXML(c).setVisible(true);
            } catch (Exception ex) {
                Logger.getLogger(FacturaXML.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /*
     Si es unatended no debe mostrar la interfaz ni ningún mensaje y salir cuando
     termine.
     Caso contrario debe mostrar la interfaz, todos los mensajes y esperar a que
     el usuario decida salir.
     */
    public void setFacnume1(int facnume) {
        this.txtFacnume1.setText(facnume + "");
        this.txtFacnume2.setText(facnume + "");
    } // end if

    public void setFacnume2(int facnume) {
        this.txtFacnume2.setText(facnume + "");
    } // end if


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboSituacionComprobante;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton radFactCompra;
    private javax.swing.JRadioButton radFactura;
    private javax.swing.JRadioButton radNC;
    private javax.swing.JRadioButton radND;
    private javax.swing.JRadioButton radTiquete;
    private javax.swing.JTextArea txaBitacora;
    private javax.swing.JFormattedTextField txtFacnume1;
    private javax.swing.JFormattedTextField txtFacnume2;
    // End of variables declaration//GEN-END:variables

    // Este método genera los XMLs por rango de documentos
    private void generateByDocument() {
        // Validación de los campos
        int facnume1;
        int facnume2;

        try {
            if (this.txtFacnume1.getText().trim().isEmpty()) {
                throw new Exception("Documento 'De' no puede quedar en blanco.");
            } // end if
            if (this.txtFacnume2.getText().trim().isEmpty()) {
                throw new Exception("Documento 'A' no puede quedar en blanco.");
            } // end if

            facnume1 = Integer.parseInt(this.txtFacnume1.getText().trim());
            facnume2 = Integer.parseInt(this.txtFacnume2.getText().trim());
            if (facnume2 < facnume1) {
                throw new Exception(
                        "Error en rango de documentos.\nEl documento 'De' debe ser inferior al documento 'A'.");
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.txtFacnume1.requestFocusInWindow();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        this.sucursal = "001";          // Solo existe un local.
        this.terminal = "00001";        // Servidor centralizado.
        this.situacionComprobante = this.cboSituacionComprobante.getSelectedIndex() + 1; // 1=Normal,2=Contingencia,3=Sin internet

        int count = 0;
        int envio; // 0=Se omitió el envío, 1=Envío satisfactorio, -1=Ocurrió un error
        for (int facnume = facnume1; facnume <= facnume2; facnume++) {
            try {
                // Antes de generar el XML valido que el número de envíos no se haya agotado
                if (envioAgotado()) { // El método tiene su propio mensaje
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                break;
            } // end try-catch

            if (this.radFactura.isSelected()) {
                envio = crearFacturaXML(facnume);
            } else if (this.radND.isSelected()) {
                envio = crearNotaDebitoXML(facnume);
            } else if (this.radNC.isSelected()) {
                envio = crearNotaCreditoXML(facnume);
            } else if (this.radTiquete.isSelected()) {
                envio = crearTiqueteXML(facnume);
            } else {
                envio = crearFacturaCompraXML(facnume);  // Julio 2019
            } // end if-else

            // Si ocurrió un error no se debe realizar el resto del proceso
            if (envio == -1) {
                continue;
            }
            // Refresco y guardo la estructura de directorios por si alguien los ha cambiado.
            DIR = new DirectoryStructure();

            count++;
            enviarXML(facnume);
            try {
                actualizarConteoEnvios();
                processLog(facnume);
            } catch (IOException | SQLException ex) {
                // Muestro el error pero dejo que continúe el proceso.
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch

        } // end for

        if (mode == INTERACTIVE && count > 0) {
            JOptionPane.showMessageDialog(null,
                    "Se generaron " + count + " documentos XML.",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
        } // end if

        // Si la bitácora contiene datos la muestro en pantalla.
        StringBuilder sb = new StringBuilder();
        if (this.bitacora != null && !this.bitacora.isEmpty()) {
            bitacora.forEach((bit) -> {
                sb.append(bit).append("\n");
            }); // end for
            this.txaBitacora.setText(sb.toString());
        } // end if
    } // end generateByDocument

    // Este método genera los XMLs por rango de fechas
    private void generateByDate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int crearFacturaXML(int facnume) {
        boolean tran = false;
        int envio = 1;
        int facnd = 0;
        String tipoXML = "V";
        DocumentoElectronico docEl;

        try {
            docEl = new DocumentoElectronico(facnume, facnd, tipoXML, conn, "1");
            docEl.setSucursal(sucursal);
            docEl.setTerminal(terminal);
            docEl.setSituacionComprobante(situacionComprobante);
            docEl.setTipoComprobante(tipoComprobante);
            // Validar si el cliente es de contado (genérico)
            boolean clienteGenerico = UtilBD.esClienteGenerico(conn, facnume, facnd);

            if (clienteGenerico) {
                throw new Exception(
                        "No puede enviar XMLs de facturas de contado. \n"
                        + "Use la opción de tiquete electrónico.");
            } // end if

            // Determinar si el documento existe o no (para saber si se actualiza o no el consecutivo).
            boolean existe = docEl.existeDoc();

            // Si el xml ya existe no se vuelve a generar.
            if (existe) {
                envio = 1;
                return envio; // No hubo error
            } // end if

            FacturaElectronica fac = new FacturaElectronica();

            fac.setAtributo1("https://cdn.comprobanteselectronicos.go.cr/xml-schemas/v4.3/facturaElectronica"); //Julio 2019
            fac.setAtributo2("http://www.w3.org/2000/09/xmldsig#");
            fac.setAtributo3("http://www.w3.org/2007/XMLSchema-versioning");
            fac.setAtributo4("http://www.w3.org/2001/XMLSchema");

            Clave clave = new Clave();
            clave.setConn(conn);
            clave.setFacnume(facnume);
            clave.setFacnd(facnd);

            // La Flor
            //fac.setCodigoActividad("155403"); // Julio 2019
            fac.setCodigoActividad(docEl.getCodigoActividad());

            // Datos del emisor
            Emisor emisor = new Emisor();
            emisor.setFacnume(facnume);
            emisor.setFacnd(facnd);
            emisor.setConnection(conn);
            emisor.setData();

            clave.setPais(emisor.getTelefono().getCodigoPais());

            // Iniciar la transacción
            CMD.transaction(conn, CMD.START_TRANSACTION);
            tran = true;

            /*
            Este método localiza primero la factura guardada en faencabe
            y si existe obtiene el consecutivo de hacienda ya guardado.
            En caso de que no logre obtener el consecutivo en esa tabla
            entonces obtendrá el siguiente consecutivo desde la tabla de
            configuración.
             */
            int documentoElectronico = docEl.getConsecutivoDocElectronico("FAC");

            clave.setSucursal(docEl.getSucursal());
            clave.setTerminal(docEl.getTerminal());
            clave.setTipoComprobante(docEl.getTipoComprobante());
            clave.setDocumento(documentoElectronico);
            clave.generarConsecutivo();

            fac.setNumeroConsecutivo(clave.getConsecutivoDoc());
            fac.setFechaEmision(emisor.getFacfech());

            clave.setSituacionComprobante(docEl.getSituacionComprobante());
            clave.setFecha(emisor.getFacfech());

            clave.setCedulaEmisor(emisor.getIdentificacion().getNumero());
            fac.setEmisor(emisor);

            // Datos del receptor
            if (!clienteGenerico) {
                Receptor receptor = new Receptor();
                receptor.setConnection(conn);
                receptor.setFacnume(facnume);
                receptor.setFacnd(facnd);
                receptor.setData();
                fac.setReceptor(receptor);
            } // end if

            fac.setCondicionVenta(emisor.getTipoVenta()); // 01=Contado, 02=Crédito

            fac.setPlazoCredito(emisor.getFacplazo());
            // ¿Qué valor se manda cuando la factura es de crédito?  
            /*
             Esto dice el documento de Estructuras de Hacienda:
             Nota: en aquellos casos en los que al momento de la emisión del 
             comprobante electrónico se desconoce el medio de pago se debe de 
             indicar “Efectivo “.
             */
            
            // 01=Efectivo, 02=Tarjeta, 03=Cheque, 04=Transferencia, 05=Recaudado por terceros, 06=SINPE MOVIL, 99=Otros
            fac.setMedioPago(emisor.getTipoPago());

            // Detalle de la factura
            DetalleFactura detalle = new DetalleFactura();
            detalle.setFacnume(facnume);
            detalle.setFacnd(facnd);
            detalle.setConnection(conn);
            detalle.setData();

            fac.setDetalle(detalle);

            // Resumen de la factura
            ResumenFactura resumen = new ResumenFactura();

            // Julio 2019
            //resumen.setCodigoMoneda(emisor.getCodigoMoneda());
            //resumen.setTipoCambio(emisor.getTipoCambio());
            CodigoTipoMoneda tipoMoneda = new CodigoTipoMoneda();
            tipoMoneda.setCodigoTipoMoneda(emisor.getCodigoMonedaHacienda());
            tipoMoneda.setTipoCambio(emisor.getTipoCambio());
            resumen.setCodigoTipoMoneda(tipoMoneda);

            resumen.setTotalServGravados(detalle.getTotalServiciosGravados());
            resumen.setTotalServExentos(detalle.getTotalServiciosExentos());

            // Por ahora esto va en cero pero si se llega a necesitar debe establecerse el valor real.
            // Julio 2019
            resumen.setTotalExento(0.00);
            resumen.setTotalMercExonerada(0.00);
            resumen.setTotalExonerado(0.00);
            resumen.setTotalIVADevuelto(0.00);
            resumen.setTotalOtrosCargos(0.00);

            resumen.setTotalMercanciasGravadas(detalle.getTotalMercanciasGravadas());
            resumen.setTotalMercanciasExentas(detalle.getTotalMercanciasExentas());
            resumen.setTotalGravado(detalle.getTotalGravado());
            resumen.setTotalExento(detalle.getTotalExcento());
            resumen.setTotalVenta(detalle.getTotalVenta());
            resumen.setTotalDescuentos(detalle.getTotalDescuentos());
            resumen.setTotalVentaNeta(detalle.getTotalVentaNeta());
            resumen.setTotalImpuesto(detalle.getTotalImpuestos());
            resumen.setTotalComprobante(detalle.getTotalComprobante());

            // Por ahora esto va en cero pero si se llega a necesitar debe establecerse el valor real.
            // Julio 2019
            resumen.setTotalExento(0.00);
            resumen.setTotalMercExonerada(0.00);
            resumen.setTotalExonerado(0.00);
            resumen.setTotalIVADevuelto(0.00);
            resumen.setTotalOtrosCargos(0.00);

            resumen.setTotalMercanciasGravadas(detalle.getTotalMercanciasGravadas());
            resumen.setTotalMercanciasExentas(detalle.getTotalMercanciasExentas());
            resumen.setTotalGravado(detalle.getTotalGravado());
            resumen.setTotalExento(detalle.getTotalExcento());
            resumen.setTotalVenta(detalle.getTotalVenta());
            resumen.setTotalDescuentos(detalle.getTotalDescuentos());
            resumen.setTotalVentaNeta(detalle.getTotalVentaNeta());
            resumen.setTotalImpuesto(detalle.getTotalImpuestos());
            resumen.setTotalComprobante(detalle.getTotalComprobante());

            fac.setResumen(resumen);

            /* Con el cambio para Junio 2019 se eliminan estos tres campos
             Normativa normativa = new Normativa();
             normativa.setNumeroResolucion("DGT-R-48-2016");
             normativa.setFechaResolucion("07-10-2016 01:00:00");
             fac.setNormativa(normativa);
             */
            // Esta parte es solo para los clientes que usan orden de compra,
            // los que no, se enviará un registro en con valor 0.
            Otros otros = new Otros();
            otros.setConnection(conn);
            otros.setData(facnume, facnd);
            //fac.setOtros(otros);

            clave.generarClave();   // Generar un número aleatorio entre 1 y 99,999,999
            clave.saveClave();

            // Se guarda la clave generada en la tabla encabezado de facturas.
            fac.setClave(clave.getClave());

            // Guardo en una propiedad la clave para utilizara en la clase DocumentoElectronico.java
            FacturaXML.claveDocumento = clave.getClave();

            String dir = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

            /*
            Nota: esta línea está generando un error desconocido que hace que
            el formulario no se muestre.  JAXBContext ctx = JAXBContext.newInstance(FacturaElectronica.class);
            Solo funciona si el sistema se invoca desde la línea de comando o desde el fuente.
            Hice múltiples pruebas incluyendo dejar el código sin uso y aún así se produce el problema.
            No se muestra ningún error ni se puede capturar con un try. 
            Bosco: 21/08/2019
             */
            // JAXB
            JAXBContext ctx = JAXBContext.newInstance(FacturaElectronica.class);
            Marshaller ms = ctx.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.marshal(fac, new File(dir + facnume + ".xml"));

            // Si el documento electrónico no existía desde antes... lo guardo.
            if (!existe) {
                saveConsecutivoDocElectronico(documentoElectronico);
            } // end if

            // Confirmar la transacción
            CMD.transaction(conn, CMD.COMMIT);
        } catch (Exception ex) {
            envio = -1;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

            if (this.mode == INTERACTIVE) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // endf if

            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Si hubo un error y se dio dentro de una transacción se debe hacer rollback
        if (envio == -1 && tran) {
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                // No se hace nada con el error porque si este error se da
                // es porque existe un problema a nivel del servidor y por
                // tanto nada va a funcionar.
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if

        return envio;
    } // end crearFacturaXML

    private int crearTiqueteXML(int facnume) {
        boolean tran = false;
        int envio = 1;
        int facnd = 0;

        try {
            // Validar si el cliente es de contado (genérico)
            boolean clienteGenerico = UtilBD.esClienteGenerico(conn, facnume, facnd);

            // Los tiquetes son exclusivos para clientes de contado.
            if (!clienteGenerico) {
                throw new Exception(
                        "No puede generar tiquetes para clientes que no son genéricos (contado).\n"
                        + "Use la opción de factura electrónica.");
            } // end if

            String tipoXML = "V";
            DocumentoElectronico docEl = new DocumentoElectronico(0, facnd, tipoXML, conn, "1");

            // Determinar si el documento existe o no (para saber si se actualiza o no el consecutivo).
            boolean existe = existeDoc(facnume, facnd);

            // Si el usuario eligió omitir los documentos generados previamente
            // y éste ya existe...
            if (existe) {
                envio = 1;
                return envio; // No hubo error
            } // end if

            TiqueteElectronico tiq = new TiqueteElectronico();

            tiq.setAtributo1("https://cdn.comprobanteselectronicos.go.cr/xml-schemas/v4.3/tiqueteElectronico");
            tiq.setAtributo2("http://www.w3.org/2000/09/xmldsig#");
            tiq.setAtributo3("http://www.w3.org/2007/XMLSchema-versioning");
            tiq.setAtributo4("http://www.w3.org/2001/XMLSchema");

            Clave clave = new Clave();
            clave.setConn(conn);
            clave.setFacnume(facnume);
            clave.setFacnd(facnd);

            //tiq.setCodigoActividad("155403"); // Julio 2019
            tiq.setCodigoActividad(docEl.getCodigoActividad());

            // Datos del emisor
            Emisor emisor = new Emisor();
            emisor.setFacnume(facnume);
            emisor.setFacnd(facnd);
            emisor.setConnection(conn);
            emisor.setData();

            clave.setPais(emisor.getTelefono().getCodigoPais());

            // Iniciar la transacción
            CMD.transaction(conn, CMD.START_TRANSACTION);
            tran = true;

            // Obtener el siguiente consecutivo de documento electrónico
            int documentoElectronico = getConsecutivoDocElectronico(facnume, facnd);

            clave.setSucursal(this.sucursal);
            clave.setTerminal(this.terminal);
            clave.setTipoComprobante(this.tipoComprobante);
            clave.setDocumento(documentoElectronico);
            clave.generarConsecutivo();

            tiq.setNumeroConsecutivo(clave.getConsecutivoDoc());
            tiq.setFechaEmision(emisor.getFacfech());

            clave.setSituacionComprobante(this.situacionComprobante);
            clave.setFecha(emisor.getFacfech());

            clave.setCedulaEmisor(emisor.getIdentificacion().getNumero());
            tiq.setEmisor(emisor);

            /*
             Estos datos no se envían en el tiquete electrónico
            
             // Datos del receptor
             if (!clienteContado) {
             Receptor receptor = new Receptor();
             receptor.setConnection(conn);
             receptor.setFacnume(facnume);
             receptor.setFacnd(facnd);
             receptor.setData();
             tiq.setReceptor(receptor);
             } // end if
             */
            tiq.setCondicionVenta(emisor.getTipoVenta()); // 01=Contado, 02=Crédito

            tiq.setPlazoCredito(emisor.getFacplazo());
            // ¿Qué valor se manda cuando la factura es de crédito?  
            /*
             Esto dice el documento de Estructuras de Hacienda:
             Nota: en aquellos casos en los que al momento de la emisión del 
             comprobante electrónico se desconoce el medio de pago se debe de 
             indicar “Efectivo “.
             */
            tiq.setMedioPago(emisor.getTipoPago()); // 01=Efectivo, 02=Tarjeta, 03=Cheque, 04=Transferencia, 05=Recaudado por terceros, 99=Otros

            // Detalle de la factura
            DetalleFactura detalle = new DetalleFactura();
            detalle.setFacnume(facnume);
            detalle.setFacnd(facnd);
            detalle.setConnection(conn);
            detalle.setData();

            tiq.setDetalle(detalle);

            // Resumen de la factura
            ResumenFactura resumen = new ResumenFactura();

            CodigoTipoMoneda tipoMoneda = new CodigoTipoMoneda();
            tipoMoneda.setCodigoTipoMoneda(emisor.getCodigoMonedaHacienda());
            tipoMoneda.setTipoCambio(emisor.getTipoCambio());
            resumen.setCodigoTipoMoneda(tipoMoneda);

            resumen.setTotalServGravados(detalle.getTotalServiciosGravados());
            resumen.setTotalServExentos(detalle.getTotalServiciosExentos());

            // Por ahora esto va en cero pero si se llega a necesitar debe establecerse el valor real.
            // Julio 2019
            resumen.setTotalExento(0.00);
            resumen.setTotalMercExonerada(0.00);
            resumen.setTotalExonerado(0.00);
            resumen.setTotalIVADevuelto(0.00);
            resumen.setTotalOtrosCargos(0.00);

            resumen.setTotalMercanciasGravadas(detalle.getTotalMercanciasGravadas());
            resumen.setTotalMercanciasExentas(detalle.getTotalMercanciasExentas());
            resumen.setTotalGravado(detalle.getTotalGravado());
            resumen.setTotalExento(detalle.getTotalExcento());
            resumen.setTotalVenta(detalle.getTotalVenta());
            resumen.setTotalDescuentos(detalle.getTotalDescuentos());
            resumen.setTotalVentaNeta(detalle.getTotalVentaNeta());
            resumen.setTotalImpuesto(detalle.getTotalImpuestos());
            resumen.setTotalComprobante(detalle.getTotalComprobante());

            // Por ahora esto va en cero pero si se llega a necesitar debe establecerse el valor real.
            // Julio 2019
            resumen.setTotalExento(0.00);
            resumen.setTotalMercExonerada(0.00);
            resumen.setTotalExonerado(0.00);
            resumen.setTotalIVADevuelto(0.00);
            resumen.setTotalOtrosCargos(0.00);

            resumen.setTotalMercanciasGravadas(detalle.getTotalMercanciasGravadas());
            resumen.setTotalMercanciasExentas(detalle.getTotalMercanciasExentas());
            resumen.setTotalGravado(detalle.getTotalGravado());
            resumen.setTotalExento(detalle.getTotalExcento());
            resumen.setTotalVenta(detalle.getTotalVenta());
            resumen.setTotalDescuentos(detalle.getTotalDescuentos());
            resumen.setTotalVentaNeta(detalle.getTotalVentaNeta());
            resumen.setTotalImpuesto(detalle.getTotalImpuestos());
            resumen.setTotalComprobante(detalle.getTotalComprobante());

            tiq.setResumen(resumen);

            /* Con el cambio para Junio 2019 se eliminan estos tres campos
             Normativa normativa = new Normativa();
             normativa.setNumeroResolucion("DGT-R-48-2016");
             normativa.setFechaResolucion("07-10-2016 01:00:00");
             fac.setNormativa(normativa);
             */
            // Esta parte es solo para los clientes que usan orden de compra,
            // los que no, se enviará un registro en con valor 0.
            Otros otros = new Otros();
            otros.setConnection(conn);
            otros.setData(facnume, facnd);
            //fac.setOtros(otros);

            clave.generarClave();   // Generar un número aleatorio entre 1 y 99,999,999
            clave.saveClave();

            // Se guarda la clave generada en la tabla encabezado de facturas.
            tiq.setClave(clave.getClave());

            // Guardo en una propiedad la clave para utilizara en la clase DocumentoElectronico.java
            FacturaXML.claveDocumento = clave.getClave();

            String dir = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

            // JAXB
            JAXBContext ctx = JAXBContext.newInstance(TiqueteElectronico.class);
            Marshaller ms = ctx.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.marshal(tiq, new File(dir + facnume + ".xml"));

            // Si el documento electrónico no existía desde antes... lo guardo.
            if (!existe) {
                saveConsecutivoDocElectronico(documentoElectronico);
            } // end if

            // Confirmar la transacción
            CMD.transaction(conn, CMD.COMMIT);
        } catch (Exception ex) {
            envio = -1;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            if (this.mode == INTERACTIVE) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Si hubo un error y se dio dentro de una transacción se debe hacer rollback
        if (envio == -1 && tran) {
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                // No se hace nada con el error porque si este error se da
                // es porque existe un problema a nivel del servidor y por
                // tanto nada va a funcionar.
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if

        return envio;
    } // end crearTiqueteXML

    private int crearNotaCreditoXML(int notanume) {
        boolean tran = false;
        int envio = 1;
        int facnd = notanume * -1;
        String tipoXML = "V";

        try {
            DocumentoElectronico docEl = new DocumentoElectronico(0, facnd, tipoXML, conn, "1");
            docEl.setSucursal(sucursal);
            docEl.setTerminal(terminal);
            docEl.setSituacionComprobante(situacionComprobante);
            docEl.setTipoComprobante(tipoComprobante);

            // Determinar si el documento existe o no (para saber si se actualiza o no el consecutivo).
            boolean existe = docEl.existeDoc();

            // Determinar si el cliente es de contado (genérico)
            boolean clienteGenerico = UtilBD.esClienteGenerico(conn, notanume, facnd);

            // Si el xml ya existe no se vuelve a generar.
            if (existe) {
                envio = 1;
                return envio; // No hubo error
            } // end if

            NotaCreditoElectronica nota = new NotaCreditoElectronica();

            nota.setAtributo1("https://cdn.comprobanteselectronicos.go.cr/xml-schemas/v4.3/notaCreditoElectronica"); // Julio 2019
            nota.setAtributo2("http://www.w3.org/2001/XMLSchema");
            nota.setAtributo3("http://www.w3.org/2001/XMLSchema-instance");

            Clave clave = new Clave();
            clave.setConn(conn);
            clave.setFacnume(notanume);
            clave.setFacnd(facnd);

            //nota.setCodigoActividad("155403"); // Julio 2019
            nota.setCodigoActividad(docEl.getCodigoActividad());

            // Datos del emisor
            Emisor emisor = new Emisor();
            emisor.setFacnume(notanume);
            emisor.setFacnd(facnd);
            emisor.setConnection(conn);
            emisor.setData();

            // Datos del receptor
            if (!clienteGenerico) {
                Receptor receptor = new Receptor();
                receptor.setConnection(conn);
                receptor.setFacnume(notanume);
                receptor.setFacnd(facnd);
                receptor.setData();
                nota.setReceptor(receptor);
            } // end if

            clave.setPais(emisor.getTelefono().getCodigoPais());

            // Iniciar la transacción
            CMD.transaction(conn, CMD.START_TRANSACTION);
            tran = true;

            // Obtener el siguiente consecutivo de documento electrónico
            int documentoElectronico = docEl.getConsecutivoDocElectronico("NCR");

            clave.setSucursal(docEl.getSucursal());
            clave.setTerminal(docEl.getTerminal());
            clave.setTipoComprobante(docEl.getTipoComprobante());
            clave.setDocumento(documentoElectronico);
            clave.generarConsecutivo();

            nota.setNumeroConsecutivo(clave.getConsecutivoDoc());
            nota.setFechaEmision(emisor.getFacfech());

            clave.setSituacionComprobante(docEl.getSituacionComprobante());
            clave.setFecha(emisor.getFacfech());

            clave.setCedulaEmisor(emisor.getIdentificacion().getNumero());
            nota.setEmisor(emisor);

            nota.setCondicionVenta("01"); // 01=Contado, 02=Crédito
            nota.setPlazoCredito(0);
            nota.setMedioPago(emisor.getTipoPago()); // 01=Efectivo, 02=Tarjeta, 03=Cheque, 04=Transferencia, 05=Recaudado por terceros, 99=Otros

            // ¿Qué valor se manda cuando la factura es de crédito?  
            /*
             Esto dice el documento de Estructuras de Hacienda:
             Nota: en aquellos casos en los que al momento de la emisión del 
             comprobante electrónico se desconoce el medio de pago se debe de 
             indicar “Efectivo “.
             */
            DetalleNotaCredito detalle = new DetalleNotaCredito();
            detalle.setFacnume(notanume);
            detalle.setFacnd(facnd);
            detalle.setConnection(conn);
            detalle.setData();

            nota.setDetalle(detalle);

            ResumenNotaCredito resumen = new ResumenNotaCredito();

            CodigoTipoMoneda tipoMoneda = new CodigoTipoMoneda();
            tipoMoneda.setCodigoTipoMoneda(emisor.getCodigoMonedaHacienda());
            tipoMoneda.setTipoCambio(emisor.getTipoCambio());
            resumen.setCodigoTipoMoneda(tipoMoneda);

            // Por ahora esto va en cero pero si se llega a necesitar debe establecerse el valor real.
            // Julio 2019
            resumen.setTotalExento(0.00);
            resumen.setTotalMercExonerada(0.00);
            resumen.setTotalExonerado(0.00);
            resumen.setTotalIVADevuelto(0.00);
            resumen.setTotalOtrosCargos(0.00);

            resumen.setTotalMercanciasGravadas(Ut.redondear(detalle.getTotalMercanciasGravadas(), 5, 3));
            resumen.setTotalMercanciasExentas(Ut.redondear(detalle.getTotalMercanciasExentas(), 5, 3));
            resumen.setTotalGravado(Ut.redondear(detalle.getTotalGravado(), 5, 3));
            resumen.setTotalExento(Ut.redondear(detalle.getTotalExcento(), 5, 3));
            resumen.setTotalVenta(Ut.redondear(detalle.getTotalVenta(), 5, 3));
            resumen.setTotalDescuentos(Ut.redondear(detalle.getTotalDescuentos(), 5, 3));
            resumen.setTotalVentaNeta(Ut.redondear(detalle.getTotalVentaNeta(), 5, 3));
            resumen.setTotalImpuesto(Ut.redondear(detalle.getTotalImpuestos(), 5, 3));
            resumen.setTotalComprobante(Ut.redondear(detalle.getTotalComprobante(), 5, 3));

            nota.setResumen(resumen);

            nota.setNota(getInformacionReferenciaNC(notanume));

            /*
             Con el cambio de julio 2019 se eliminan estos campos
             Normativa normativa = new Normativa();
             normativa.setNumeroResolucion("DGT-R-48-2016");
             normativa.setFechaResolucion("07-10-2016 01:00:00");
             nota.setNormativa(normativa);
             */
            // Esta parte es solo para los clientes que usan orden de compra,
            // los que no, se enviará un registro en con valor 0.
            Otros otros = new Otros();
            otros.setConnection(conn);
            otros.setData(notanume, facnd);
            nota.setOtros(otros);

            clave.generarClave();   // Generar un número aleatorio entre 1 y 99,999,999
            clave.saveClave();
            nota.setClave(clave.getClave());

            // Guardo en una propiedad la clave para utilizara en la clase DocumentoElectronico.java
            FacturaXML.claveDocumento = clave.getClave();

            String dir = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

            // JAXB
            JAXBContext ctx = JAXBContext.newInstance(NotaCreditoElectronica.class);
            Marshaller ms = ctx.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.marshal(nota, new File(dir + (notanume) + ".xml"));

            // Si el documento electrónico no existía desde antes... lo guardo.
            if (!existe) {
                saveConsecutivoDocElectronico(documentoElectronico);
            } // end if

            // Confirmar la transacción
            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException | JAXBException ex) {
            envio = -1;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            if (this.mode == INTERACTIVE) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Si hubo un error y se dio dentro de una transacción se debe hacer rollback
        if (envio == -1 && tran) {
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                // No se hace nada con el error porque si este error se da
                // es porque existe un problema a nivel del servidor y por
                // tanto nada va a funcionar.
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if

        return envio;
    } // end crearNotaCreditoXML

    private int crearNotaDebitoXML(int facnume) {
        boolean tran = false;
        int envio = 1;
        int facnd = facnume * -1;
        String tipoXML = "V";

        try {
            DocumentoElectronico docEl = new DocumentoElectronico(0, facnd, tipoXML, conn, "1");
            docEl.setSucursal(sucursal);
            docEl.setTerminal(terminal);
            docEl.setSituacionComprobante(situacionComprobante);
            docEl.setTipoComprobante(tipoComprobante);

            // Determinar si el documento existe o no (para saber si se actualiza o no el consecutivo).
            boolean existe = docEl.existeDoc();

            if (existe) {
                envio = 1;
                return envio; // No hubo error
            } // end if

            NotaDebitoElectronica notaD = new NotaDebitoElectronica();

            notaD.setAtributo1("https://cdn.comprobanteselectronicos.go.cr/xml-schemas/v4.3/notaDebitoElectronica"); //Julio 2019
            notaD.setAtributo2("http://www.w3.org/2000/09/xmldsig#");
            notaD.setAtributo3("http://www.w3.org/2007/XMLSchema-versioning");
            notaD.setAtributo4("http://www.w3.org/2001/XMLSchema");

            Clave clave = new Clave();
            clave.setConn(conn);
            clave.setFacnume(facnume);
            clave.setFacnd(facnd);

            //notaD.setCodigoActividad("155403"); // Julio 2019
            notaD.setCodigoActividad(docEl.getCodigoActividad());

            // Datos del emisor
            Emisor emisor = new Emisor();
            emisor.setFacnume(facnume);
            emisor.setFacnd(facnd);
            emisor.setConnection(conn);
            emisor.setData();

            clave.setPais(emisor.getTelefono().getCodigoPais());

            // Iniciar la transacción
            CMD.transaction(conn, CMD.START_TRANSACTION);
            tran = true;

            // Obtener el siguiente consecutivo de factura electrónica
            int documentoElectronico = docEl.getConsecutivoDocElectronico("NDB");

            clave.setSucursal(docEl.getSucursal());
            clave.setTerminal(docEl.getTerminal());
            clave.setTipoComprobante(docEl.getTipoComprobante());
            clave.setDocumento(documentoElectronico);
            clave.generarConsecutivo();

            notaD.setNumeroConsecutivo(clave.getConsecutivoDoc());
            notaD.setFechaEmision(emisor.getFacfech());

            clave.setSituacionComprobante(docEl.getSituacionComprobante());
            clave.setFecha(emisor.getFacfech());

            clave.setCedulaEmisor(emisor.getIdentificacion().getNumero());
            notaD.setEmisor(emisor);

            // Datos del receptor
            Receptor receptor = new Receptor();
            receptor.setConnection(conn);
            receptor.setFacnume(facnume);
            receptor.setFacnd(facnd);
            receptor.setData();

            notaD.setReceptor(receptor);

            notaD.setCondicionVenta(emisor.getTipoVenta()); // 01=Contado, 02=Crédito

            notaD.setPlazoCredito(emisor.getFacplazo());
            // ¿Qué valor se manda cuando la factura es de crédito?  
            /*
             Esto dice el documento de Estructuras de Hacienda:
             Nota: en aquellos casos en los que al momento de la emisión del 
             comprobante electrónico se desconoce el medio de pago se debe de 
             indicar “Efectivo “.
             */
            notaD.setMedioPago(emisor.getTipoPago()); // 01=Efectivo, 02=Tarjeta, 03=Cheque, 04=Transferencia, 05=Recaudado por terceros, 99=Otros

            DetalleFactura detalle = new DetalleFactura();
            detalle.setFacnume(facnume);
            detalle.setFacnd(facnd);
            detalle.setConnection(conn);
            detalle.setData();

            notaD.setDetalle(detalle);

            ResumenFactura resumen = new ResumenFactura();

            // Julio 2019
            CodigoTipoMoneda tipoMoneda = new CodigoTipoMoneda();
            tipoMoneda.setCodigoTipoMoneda(emisor.getCodigoMonedaHacienda());
            tipoMoneda.setTipoCambio(emisor.getTipoCambio());
            resumen.setCodigoTipoMoneda(tipoMoneda);
            resumen.setTotalServGravados(detalle.getTotalServiciosGravados());
            resumen.setTotalServExentos(detalle.getTotalServiciosExentos());

            // Por ahora esto va en cero pero si se llega a necesitar debe establecerse el valor real.
            // Julio 2019
            resumen.setTotalExento(0.00);
            resumen.setTotalMercExonerada(0.00);
            resumen.setTotalExonerado(0.00);
            resumen.setTotalIVADevuelto(0.00);
            resumen.setTotalOtrosCargos(0.00);

            resumen.setTotalMercanciasGravadas(detalle.getTotalMercanciasGravadas());
            resumen.setTotalMercanciasExentas(detalle.getTotalMercanciasExentas());
            resumen.setTotalGravado(detalle.getTotalGravado());
            resumen.setTotalExento(detalle.getTotalExcento());
            resumen.setTotalVenta(detalle.getTotalVenta() + resumen.getTotalExonerado());
            resumen.setTotalDescuentos(detalle.getTotalDescuentos());
            resumen.setTotalVentaNeta(detalle.getTotalVentaNeta() - detalle.getTotalDescuentos());
            resumen.setTotalImpuesto(detalle.getTotalImpuestos());
            resumen.setTotalComprobante(detalle.getTotalComprobante() + resumen.getTotalOtrosCargos() - resumen.getTotalIVADevuelto());

            notaD.setResumen(resumen);

            //fac.setNota(setInformacionReferenciaND(facnume));

            /* Con el cambio de Julio 2019 estos campos se eliminan
             Normativa normativa = new Normativa();
             normativa.setNumeroResolucion("DGT-R-48-2016");
             normativa.setFechaResolucion("07-10-2016 01:00:00");
             fac.setNormativa(normativa);
             */
            // Esta parte es solo para los clientes que usan orden de compra,
            // los que no, se enviará un registro en con valor 0.
            Otros otros = new Otros();
            otros.setConnection(conn);
            otros.setData(facnume, facnd);
            //fac.setOtros(otros);

            clave.generarClave();   // Generar un número aleatorio entre 1 y 99,999,999
            clave.saveClave();
            notaD.setClave(clave.getClave());

            // Guardo en una propiedad la clave para utilizara en la clase DocumentoElectronico.java
            FacturaXML.claveDocumento = clave.getClave();

            String dir = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

            // JAXB
            JAXBContext ctx = JAXBContext.newInstance(NotaDebitoElectronica.class);
            Marshaller ms = ctx.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.marshal(notaD, new File(dir + facnume + ".xml"));

            // Si el documento electrónico no existía desde antes... lo guardo.
            if (!existe) {
                saveConsecutivoDocElectronico(documentoElectronico);
            } // end if

            // Confirmar la transacción
            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException | JAXBException ex) {
            envio = -1;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            if (this.mode == INTERACTIVE) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Si hubo un error y se dio dentro de una transacción se debe hacer rollback
        if (envio == -1 && tran) {
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                // No se hace nada con el error porque si este error se da
                // es porque existe un problema a nivel del servidor y por
                // tanto nada va a funcionar.
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if

        return envio;
    } // end crearNotaDebitoXML

    /**
     * Obtiene el consecutivo para un documento electrónico nuevo o generado previamente.
     *
     * @param facnume
     * @param facnd
     * @return
     * @throws SQLException
     */
    private int getConsecutivoDocElectronico(int facnume, int facnd) throws SQLException {
        int consecutivo = 0;
        // Primero consulto si el documento ya existe.
        String sqlSent
                = "Select  "
                + "	Cast(Substring(right(claveHacienda,19),1,10) as SIGNED) as consecutivo "
                + "from faencabe  "
                + "where facnume = ? and facnd = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                consecutivo = rs.getInt(1);
            } // end if
            ps.close();
        } // end try

        // Si se obtuvo un consecutivo entonces lo retorno porque significa
        // que el documento electrónico fue generado previamente.
        if (consecutivo > 0) {
            return consecutivo;
        } // end if
        //... caso contrario hago el proceso normal para obtener el siguiente consecutivo.

        // Establecer el nombre del campo según el tipo de documento
        String campo;

        if (this.radFactura.isSelected()) {
            campo = "facElect";
        } else if (this.radND.isSelected()) {
            campo = "ndebElect";
        } else if (this.radNC.isSelected()) {
            campo = "ncredElect";
        } else {
            campo = "tiqElect";
        } // end if-else

        sqlSent = "Select " + campo + " from config";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                consecutivo = rs.getInt(campo) + 1; // La tabla siempre guarda el último número usado.
            } // end if
            ps.close();
        } // end try
        return consecutivo;
    } // end getConsecutivoDocElectronico

    /**
     * Obtiene el consecutivo para un documento electrónico nuevo o generado previamente
     * (factura de compra).
     *
     * @param factura
     * @param tipo
     * @return
     * @throws SQLException
     */
    private int getConsecutivoDocElectronicoCompra(String factura, String tipo) throws SQLException {
        int consecutivo = 0;
        // Primero consulto si el documento ya existe.
        String sqlSent
                = "Select   "
                + "	Cast(Substring(right(claveHacienda,19),1,10) as SIGNED) as consecutivo  "
                + "from cxpfacturas   "
                + "where factura = ? and tipo = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, factura);
            ps.setString(2, tipo);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                consecutivo = rs.getInt(1);
            } // end if
            ps.close();
        } // end try

        // Si se obtuvo un consecutivo entonces lo retorno porque significa
        // que el documento electrónico fue generado previamente.
        if (consecutivo > 0) {
            return consecutivo;
        } // end if
        //... caso contrario hago el proceso normal para obtener el siguiente consecutivo.

        sqlSent = "Select facElectCompra from config";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                consecutivo = rs.getInt("facElectCompra") + 1; // La tabla siempre guarda el último número usado.
            } // end if
            ps.close();
        } // end try
        return consecutivo;
    } // end getConsecutivoDocElectronicoCompra

    private void saveConsecutivoDocElectronico(int consecutivo) throws SQLException {
        // Establecer el nombre del campo según el tipo de documento
        String campo;

        if (this.radFactura.isSelected()) {
            campo = "facElect";
        } else if (this.radND.isSelected()) {
            campo = "ndebElect";
        } else if (this.radNC.isSelected()) {
            campo = "ncredElect";
        } else if (this.radFactCompra.isSelected()) {
            campo = "facElectCompra";
        } else {
            campo = "tiqElect";
        } // end if-else

        String sqlSent
                = "Update config set " + campo + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, consecutivo);
            CMD.update(ps);
        } // end try
    } // end saveConsecutivoDocElectronico

    public int getMode() {
        return mode;
    }

    // Determina si está en modo interactivo o desatendido
    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setTipo(int tipo) {
        switch (tipo) {
            case FACTURA:
                this.radFactura.setSelected(true);
                this.radFacturaActionPerformed(null);
                break;

            case NOTADB:
                this.radND.setSelected(true);
                this.radNDActionPerformed(null);
                break;

            case NOTACR:
                this.radNC.setSelected(true);
                this.radNCActionPerformed(null);
                break;

            case TIQUETE:
                this.radTiquete.setSelected(true);
                this.radTiqueteActionPerformed(null);
                break;

            default:
                this.radFactura.setSelected(true);
                this.radFacturaActionPerformed(null);
        } // end switch
    } // end setTipo

    public void setRangoDocumentos(int doc1, int doc2) {
        this.txtFacnume1.setText(doc1 + "");
        this.txtFacnume2.setText(doc2 + "");
    } // end setRangoDocumentos

    // Este método solo corre si se encuentra en modo desatendido
    public void runApp() {
        if (this.mode == FacturaXML.INTERACTIVE) {
            return;
        } // end if

        btnGuardarActionPerformed(null);
        btnSalirActionPerformed(null);
    } // end runApp

    private void enviarXML(int facnume) {
        int facnd = 0; // Las facturas, los tiquetes y las facturas de compra deben llevar facnd en cero.
        if (this.radNC.isSelected()
                || this.radND.isSelected()) { // Notas de crédito y débito
            facnd = facnume * -1;
        }  // end if

        String tipoXML = radFactCompra.isSelected() ? "C" : "V";

        // Este proceso es únicamente windows por lo que no debe correr en Linux
        String os = Ut.getProperty(Ut.OS_NAME).toLowerCase();
        if (!os.contains("win") || !Menu.enviarDocumentosElectronicos) {
            return;
        } // end if

        try {
            DocumentoElectronico docEl = new DocumentoElectronico(facnume, facnd, tipoXML, conn, "1");
            docEl.setSituacionComprobante(situacionComprobante);
            docEl.setSucursal(sucursal);
            docEl.setTerminal(terminal);
            docEl.setTipoComprobante(tipoComprobante);

            docEl.enviarXML();
            if (docEl.isError()) {
                throw new Exception(docEl.getError_msg());
            }
        } catch (Exception ex) {
            if (mode == INTERACTIVE) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

    } // end enviarXML

    /**
     * Determinar si existe un documento de ventas.
     *
     * @param facnume int número de documento
     * @param facnd int determina el tipo de documento (0=Factura, >0=Nota de crédito,
     * <0=Nota de débito) @return @ throws SQ LException
     */
    private boolean existeDoc(int facnume, int facnd) throws SQLException {
        boolean existe = false;
        String sqlSent
                = "Select  "
                + "	claveHacienda "
                + "from faencabe  "
                + "where facnume = ? "
                + "and facnd = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                String clave = rs.getString(1).trim();
                if (!clave.isEmpty() && !clave.equals("-1")) {
                    existe = true;
                } // end if
            } // end if
            ps.close();
        } // end try

        return existe;
    } // end existeDoc

    /**
     * Determinar si un documento de compra existe.
     *
     * @param factura String número de documento
     * @param tipo String tipo de documento (FAC=Factura, NCR=Nota de crédito, NDB=Nota de
     * débito)
     * @return
     * @throws SQLException
     */
    private boolean existeDoc(String factura, String tipo) throws SQLException {
        boolean existe = false;
        String sqlSent
                = "Select  "
                + "	claveHacienda "
                + "from cxpfacturas  "
                + "where factura = ? "
                + "and tipo = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, factura);
            ps.setString(2, tipo);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                String clave = rs.getString(1).trim();
                if (!clave.isEmpty()) {
                    existe = true;
                } // end if
            } // end if
            ps.close();
        } // end try

        return existe;
    } // end existeDoc

    // Este método agrega a una lista los resultados del envío de cada documento electrónico
    private void processLog(int facnume) throws FileNotFoundException, IOException {
        String dir = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

        // Si es una factura de compra el nombre del archivo debe llevar _C antes de la extensión.
        String fileName = dir + facnume + (radFactCompra.isSelected() ? "_C.txt" : ".txt");
        File f = new File(fileName);
        if (!f.exists()) {
            return;
        } // end if

        FileReader lector = new FileReader(f.getAbsolutePath());
        BufferedReader b = new BufferedReader(lector);
        String linea;
        this.bitacora.add("Documento " + facnume + "\n");
        while ((linea = b.readLine()) != null) {
            this.bitacora.add(linea);
        } // end while
        b.close();
        lector.close();
    } // end processLog

    private boolean proveedorContado(int facnume, int facnd) throws SQLException {
        boolean contado = false;
        String sqlSent
                = "Select "
                + "	inclient.cligenerico  "
                + "from faencabe  "
                + "Inner join inclient on faencabe.clicode = inclient.clicode "
                + "where faencabe.facnume = ?  "
                + "and faencabe.facnd = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                int valor = rs.getInt(1);
                contado = (valor > 0);
            } // end if
            ps.close();
        } // end try

        return contado;
    } // end proveedorContado

    /**
     * Este método retorna una lista con todas aquellas facturas o ND afectadas por la
     * nota de crédito (no funciona para notas de crédito sobre contado).
     *
     * @param notanume
     * @return
     * @throws SQLException
     */
    private List<InformacionReferencia> getInformacionReferenciaNC(int notanume) throws SQLException {
        List<InformacionReferencia> ref = new ArrayList<>();

        String sqlSent
                = "Select  "
                + "	notasd.facnume, "
                + "	notasd.facnd, "
                + "	faencabe.facfech, "
                + "	faencabe.claveHacienda "
                + "from notasd "
                + "Inner join faencabe on notasd.facnume = faencabe.facnume and notasd.facnd = faencabe.facnd "
                + "where notasd.Notanume = ?";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, notanume);
        ResultSet rs = CMD.select(ps);

        if (!rs.first()) {
            ps.close();
            throw new SQLException("[InformacionReferencia] Nota de crédito no " + notanume + " encontrada.");
        } // end if

        rs.beforeFirst();

        while (rs.next()) {
            // Si la factura o ND no ha sido enviada a Hacienda no se puede
            // usar como referencia.
            if (rs.getString("claveHacienda").trim().isEmpty()) {
                int facnume = rs.getInt("facnume");
                ps.close();
                throw new SQLException("[InformacionReferencia] Esta nota de crédito\n"
                        + "hace referencia a la factura/ND # " + facnume + " pero\n"
                        + "parece que ese documento aún no ha sido enviado a Hacienda.\n\n"
                        + "Recomendación: Si la factura o ND no ha sido enviada a\n"
                        + "Hacienda, puede anularla sin necesidad de hacer NC. Pero"
                        + "como ya creó esta NC, debe anular ambas (primero la NC).");
            } // end if

            InformacionReferencia r = new InformacionReferencia();
            r.setCodigo("04"); // Referencia a otro documento
            r.setFechaEmision(rs.getDate("facfech"));
            r.setNumero(rs.getString("claveHacienda"));
            r.setRazon("Nota de crédito - varios");
            r.setTipodoc(rs.getInt("facnd") == 0 ? "01" : "02");    // Una NC solo puede aplicar a una factura o a una ND.

            ref.add(r);
        } // end while

        return ref;
    } // end getInformacionReferenciaNC

    /**
     * Este método asigna como referencia una nota de crédito.
     *
     * @param notanume
     * @return
     * @throws SQLException
     */
    private List<InformacionReferencia> setInformacionReferenciaND(int notanume) throws SQLException {
        List<InformacionReferencia> ref = new ArrayList<>();

        String sqlSent
                = "Select  "
                + "	b.facnume, "
                + "	b.facnd, "
                + "	b.facfech,   "
                + "	b.claveHacienda  "
                + "From faencabe a "
                + "Inner join faencabe b on a.referencia = b.facnume and abs(a.referencia) = b.facnd "
                + "Where a.facnume = ? "
                + "and a.facnd = (? *-1)";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, notanume);
        ps.setInt(2, notanume);

        ResultSet rs = CMD.select(ps);

        if (!rs.first()) {
            ps.close();
            throw new SQLException("[InformacionReferencia] Nota de débito no " + notanume + " encontrada.");
        } // end if

        rs.beforeFirst();

        while (rs.next()) {
            InformacionReferencia r = new InformacionReferencia();
            r.setCodigo("04"); // Referencia a otro documento
            r.setFechaEmision(rs.getDate("facfech"));
            r.setNumero(rs.getString("claveHacienda"));
            r.setRazon("Nota de débito - varios");
            r.setTipodoc("03");    // Una ND solo puede aplicar a una una NC o a un pago. (03=NC)
            //r.setTipodoc("02");

            ref.add(r);
        } // end while

        return ref;
    } // end setInformacionReferenciaND

    private int crearFacturaCompraXML(int facnume) {
        boolean tran = false;
        int envio = 1;

        try {
            // Determinar si el documento existe o no (para saber si se actualiza o no el consecutivo).
            boolean existe = existeDoc(facnume + "", "FAC");

            if (existe) {
                envio = 0;
                return envio; // No hubo error
            } // end if

            FacturaElectronicaCompra fac = new FacturaElectronicaCompra();

            fac.setAtributo1("https://cdn.comprobanteselectronicos.go.cr/xml-schemas/v4.3/facturaElectronicaCompra");
            fac.setAtributo2("http://www.w3.org/2000/09/xmldsig#");
            fac.setAtributo3("http://www.w3.org/2007/XMLSchema-versioning");
            fac.setAtributo4("http://www.w3.org/2001/XMLSchema");

            fac.setCodigoActividad("155403");

            Clave clave = new Clave();
            clave.setConn(conn);
            clave.setFacturaCompra(facnume + "");
            clave.setTipoFacturaCompra("FAC");

            // Datos del emisor (en el caso de compras el emisor es el proveedor)
            Emisor emisor = new Emisor();
            emisor.setFacturaCompra(facnume + "");
            emisor.setTipoFacturaCompra("FAC");
            emisor.setConnection(conn);
            emisor.setDataCompras();

            clave.setPais(emisor.getTelefono().getCodigoPais());

            // Iniciar la transacción
            CMD.transaction(conn, CMD.START_TRANSACTION);
            tran = true;

            // Obtener el siguiente consecutivo de factura electrónica de compra
            int documentoElectronico = getConsecutivoDocElectronicoCompra(facnume + "", "FAC");

            clave.setSucursal(this.sucursal);
            clave.setTerminal(this.terminal);
            clave.setTipoComprobante(this.tipoComprobante);
            clave.setDocumento(documentoElectronico);
            clave.generarConsecutivo();

            fac.setNumeroConsecutivo(clave.getConsecutivoDoc());
            fac.setFechaEmision(emisor.getFacfech());

            clave.setSituacionComprobante(this.situacionComprobante);
            clave.setFecha(emisor.getFacfech());

            clave.setCedulaEmisor(emisor.getIdentificacion().getNumero());
            fac.setEmisor(emisor);

            // Datos del receptor.  En la factura de compra el receptor somos nosotros
            Receptor receptor = new Receptor();
            receptor.setConnection(conn);
            receptor.setFacturaCompra(facnume + "");
            receptor.setTipoFacturaCompra("FAC"); // Este tipo es el que va en la tabla cxpfacturas
            receptor.setDataCompras();
            fac.setReceptor(receptor);

            fac.setCondicionVenta(emisor.getTipoVenta()); // 01=Contado, 02=Crédito

            fac.setPlazoCredito(emisor.getFacplazo());
            // ¿Qué valor se manda cuando la factura es de crédito?  
            /*
             Esto dice el documento de Estructuras de Hacienda:
             Nota: en aquellos casos en los que al momento de la emisión del 
             comprobante electrónico se desconoce el medio de pago se debe de 
             indicar “Efectivo “.
             */
            fac.setMedioPago(emisor.getTipoPago()); // 01=Efectivo, 02=Tarjeta, 03=Cheque, 04=Transferencia, 05=Recaudado por terceros, 99=Otros

            // Detalle de la factura
            DetalleFactura detalle = new DetalleFactura();
            detalle.setFacturaCompra(facnume + "");
            detalle.setTipoFacturaCompra("FAC");
            detalle.setConnection(conn);
            detalle.setDataCampras();

            fac.setDetalle(detalle);

            // Resumen de la factura
            ResumenFactura resumen = new ResumenFactura();

            // Julio 2019
            CodigoTipoMoneda tipoMoneda = new CodigoTipoMoneda();
            tipoMoneda.setCodigoTipoMoneda(emisor.getCodigoMonedaHacienda());
            tipoMoneda.setTipoCambio(emisor.getTipoCambio());
            resumen.setCodigoTipoMoneda(tipoMoneda);

            resumen.setTotalServGravados(detalle.getTotalServiciosGravados());
            resumen.setTotalServExentos(detalle.getTotalServiciosExentos());

            // Por ahora esto va en cero pero si se llega a necesitar debe establecerse el valor real.
            // Julio 2019
            resumen.setTotalExento(0.00);
            resumen.setTotalMercExonerada(0.00);
            resumen.setTotalExonerado(0.00);
            resumen.setTotalIVADevuelto(null); // Con esto se elimina el campo a la hora de generar el xml
            resumen.setTotalOtrosCargos(0.00);

            resumen.setTotalMercanciasGravadas(detalle.getTotalMercanciasGravadas());
            resumen.setTotalMercanciasExentas(detalle.getTotalMercanciasExentas());
            resumen.setTotalGravado(detalle.getTotalGravado());
            resumen.setTotalExento(detalle.getTotalExcento());
            resumen.setTotalVenta(detalle.getTotalVenta() + resumen.getTotalExonerado());
            resumen.setTotalDescuentos(detalle.getTotalDescuentos());
            resumen.setTotalVentaNeta(detalle.getTotalVentaNeta() - detalle.getTotalDescuentos());
            resumen.setTotalImpuesto(detalle.getTotalImpuestos());
            resumen.setTotalComprobante(detalle.getTotalComprobante() + resumen.getTotalOtrosCargos());
            fac.setResumen(resumen);

            /* Con el cambio para Junio 2019 se eliminan estos tres campos
             Normativa normativa = new Normativa();
             normativa.setNumeroResolucion("DGT-R-48-2016");
             normativa.setFechaResolucion("07-10-2016 01:00:00");
             fac.setNormativa(normativa);
             */
            clave.generarClave();   // Generar un número aleatorio entre 1 y 99,999,999

            // Se guarda la clave generada en la tabla de facturas de compra.
            clave.saveClaveCompras();

            fac.setClave(clave.getClave());

            // Guardo en una propiedad la clave para utilizara en la clase DocumentoElectronico.java
            FacturaXML.claveDocumento = clave.getClave();

            String dir = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

            // JAXB
            JAXBContext ctx = JAXBContext.newInstance(FacturaElectronicaCompra.class);
            Marshaller ms = ctx.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.marshal(fac, new File(dir + facnume + "_C.xml"));

            // Si el documento electrónico no existía desde antes... lo guardo.
            if (!existe) {
                saveConsecutivoDocElectronico(documentoElectronico);
            } // end if

            // Confirmar la transacción
            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException | JAXBException ex) {
            envio = -1;
            Logger.getLogger(GeneraXML.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Si hubo un error y se dio dentro de una transacción se debe hacer rollback
        if (envio == -1 && tran) {
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex) {
                Logger.getLogger(FacturaXML.class.getName()).log(Level.SEVERE, null, ex);
                // No se hace nada con el error porque si este error se da
                // es porque existe un problema a nivel del servidor y por
                // tanto nada va a funcionar.
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if

        return envio;
    } // end crearFacturaCompraXML

    public String getRespuestaHacienda() {
        return this.txaBitacora.getText();
    } // end getRespuestaHacienda

} // end class
