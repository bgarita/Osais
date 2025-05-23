/*
 * Bodegas.java
 *
 * Created on 11/10/2010, 10:00:00 AM
 */
package interfase.mantenimiento;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.otros.Navegador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import Exceptions.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class TarifasExpress extends JFrame {

    public ResultSet rs, rs3;
    private String tabla;
    private Statement sqlquery;
    private Connection conn = null;
    Navegador nav = null;
    private ResultSet rs2 = null;
    private javax.swing.JTextField codigoExp;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form TarifasExpress
     */
    public TarifasExpress(Connection c, javax.swing.JTextField codigoExp) {
        initComponents();

        conn = c;
        this.codigoExp = codigoExp;
        // Por ahora se deshabilita este código 14/01/2012.
        // ********************************************************************
        // Si el parámetro recibido es 00 es porque fue 
        // llamado desde un lugar en donde lo único que
        // se requiere es el valor seleccionado y de esa
        // forma evitar el mantenimiento; es dicir guardar
        // y borrar. (Ej.: La factura)
        //        if (this.codigoExp.getText().trim().equals("00")){
        //            cmdGuardar.setEnabled(false);
        //            cmdBorrar.setEnabled(false);
        //            mnuGuardar.setEnabled(false);
        //            txtCodExpress.setEditable(false);
        //            txtMinimo.setEditable(false);
        //            txtTarifa.setEditable(false);
        //            spnPorcentaje.setEnabled(false);
        //        } // end if
        // ********************************************************************

        txtCodExpress.setText("0");
        txtMinimo.setText("0.00");
        txtTarifa.setText("0.00");

        tabla = "faexpress";
        nav = new Navegador();
        nav.setConexion(conn);

        try {
            sqlquery = conn.createStatement();
            rs = nav.cargarRegistro(
                    Navegador.PRIMERO,
                    0,
                    tabla,
                    "CodExpress");
            if (rs == null) {
                return;
            } // end if

            if (!rs.first()) {
                return;
            } // end if
            txtCodExpress.setText(rs.getString("CodExpress"));
            txtMinimo.setText(
                    Ut.setDecimalFormat(
                            rs.getString("Minimo"), "#,##0.00"));
            txtTarifa.setText(
                    Ut.setDecimalFormat(
                            rs.getString("Tarifa"), "#,##0.00"));
            spnPorcentaje.setValue(rs.getFloat("Porcentaje"));

            this.codigoExp.setText(rs.getString("CodExpress"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

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

        lblFamilia = new javax.swing.JLabel();
        txtCodExpress = new javax.swing.JFormattedTextField();
        cmdPrimero = new javax.swing.JButton();
        cmdAnterior = new javax.swing.JButton();
        cmdSiguiente = new javax.swing.JButton();
        cmdUltimo = new javax.swing.JButton();
        cmdGuardar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtTarifa = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtMinimo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        spnPorcentaje = new javax.swing.JSpinner();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuAyuda = new javax.swing.JMenu();
        mnuHelp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de Tarifas Express");

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Código");

        txtCodExpress.setColumns(3);
        txtCodExpress.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtCodExpress.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCodExpress.setToolTipText("Código de tarifa");
        txtCodExpress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodExpressActionPerformed(evt);
            }
        });
        txtCodExpress.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCodExpressFocusGained(evt);
            }
        });

        cmdPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZTOP.png"))); // NOI18N
        cmdPrimero.setToolTipText("Ir al primer registro");
        cmdPrimero.setFocusCycleRoot(true);
        cmdPrimero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPrimeroActionPerformed(evt);
            }
        });

        cmdAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZBACK.png"))); // NOI18N
        cmdAnterior.setToolTipText("Ir al registro anterior");
        cmdAnterior.setFocusCycleRoot(true);
        cmdAnterior.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAnteriorActionPerformed(evt);
            }
        });

        cmdSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZNEXT.png"))); // NOI18N
        cmdSiguiente.setToolTipText("Ir al siguiente registro");
        cmdSiguiente.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSiguienteActionPerformed(evt);
            }
        });

        cmdUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZEND.png"))); // NOI18N
        cmdUltimo.setToolTipText("Ir al último registro");
        cmdUltimo.setFocusCycleRoot(true);
        cmdUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdUltimoActionPerformed(evt);
            }
        });

        cmdGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        cmdGuardar.setToolTipText("Guardar registro");
        cmdGuardar.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdGuardarActionPerformed(evt);
            }
        });

        cmdBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZDELETE.png"))); // NOI18N
        cmdBorrar.setToolTipText("Borrar registro");
        cmdBorrar.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBorrarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Tarifa mínima");

        txtTarifa.setColumns(3);
        txtTarifa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTarifa.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTarifa.setToolTipText("Monto mínimo de la tarifa a cobrar");
        txtTarifa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTarifaActionPerformed(evt);
            }
        });
        txtTarifa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTarifaFocusGained(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Monto mínimo");

        txtMinimo.setColumns(3);
        txtMinimo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMinimo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMinimo.setToolTipText("Monto mínimo de la tarifa a cobrar");
        txtMinimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMinimoActionPerformed(evt);
            }
        });
        txtMinimo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMinimoFocusGained(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Porcentaje");

        spnPorcentaje.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.5f), Float.valueOf(99.0f), Float.valueOf(0.5f)));
        spnPorcentaje.setToolTipText("% a cobrar cuando el monto es inferior al mínimo");
        spnPorcentaje.setEditor(new javax.swing.JSpinner.NumberEditor(spnPorcentaje, "#,##0.00"));

        mnuArchivo.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuSalir);

        jMenuBar1.add(mnuArchivo);

        mnuEdicion.setText("Edición");

        mnuBorrar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        mnuBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        mnuBorrar.setText("Borrar");
        mnuBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBorrar);

        jMenuBar1.add(mnuEdicion);

        mnuAyuda.setText("Ayuda");

        mnuHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mnuHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user_comment.png"))); // NOI18N
        mnuHelp.setText("Sobre esta pantalla");
        mnuHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHelpActionPerformed(evt);
            }
        });
        mnuAyuda.add(mnuHelp);

        jMenuBar1.add(mnuAyuda);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cmdPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblFamilia)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCodExpress, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblFamilia)
                            .addComponent(txtCodExpress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel1)
                            .addComponent(txtTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel2)
                            .addComponent(txtMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(spnPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdPrimero)
                    .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        setSize(new java.awt.Dimension(472, 240));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed

        eliminarRegistro(txtCodExpress.getText().trim());
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void txtCodExpressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodExpressActionPerformed
        refrescarTarifa(Navegador.ESPECIFICO);
        txtCodExpress.transferFocus();
}//GEN-LAST:event_txtCodExpressActionPerformed

    private void cmdPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrimeroActionPerformed
        refrescarTarifa(Navegador.PRIMERO);
}//GEN-LAST:event_cmdPrimeroActionPerformed

    private void cmdAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnteriorActionPerformed
        refrescarTarifa(Navegador.ANTERIOR);
}//GEN-LAST:event_cmdAnteriorActionPerformed

    private void cmdSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSiguienteActionPerformed
        refrescarTarifa(Navegador.SIGUIENTE);
}//GEN-LAST:event_cmdSiguienteActionPerformed

    private void cmdUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUltimoActionPerformed
        refrescarTarifa(Navegador.ULTIMO);
}//GEN-LAST:event_cmdUltimoActionPerformed

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        guardarRegistro();
}//GEN-LAST:event_cmdGuardarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        eliminarRegistro(txtCodExpress.getText().trim());
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void txtCodExpressFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodExpressFocusGained
        txtCodExpress.selectAll();
    }//GEN-LAST:event_txtCodExpressFocusGained

    private void txtTarifaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTarifaActionPerformed
        txtTarifa.transferFocus();
    }//GEN-LAST:event_txtTarifaActionPerformed

    private void txtTarifaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTarifaFocusGained
        txtTarifa.selectAll();
    }//GEN-LAST:event_txtTarifaFocusGained

    private void txtMinimoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinimoFocusGained
        txtMinimo.selectAll();
    }//GEN-LAST:event_txtMinimoFocusGained

    private void txtMinimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMinimoActionPerformed
        txtMinimo.transferFocus();
    }//GEN-LAST:event_txtMinimoActionPerformed

    private void mnuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHelpActionPerformed
        String message;
        message
                = "Si el monto de la factura o servicio es mayor o igual al\n"
                + "MONTO MÍNIMO entoces se plicará el monto que aparece en \n"
                + "TARIFA MÍNIMA.  De lo contrario se aplicará el PORCENTAJE.";
        JOptionPane.showMessageDialog(
                null,
                message, "Ayuda",
                JOptionPane.INFORMATION_MESSAGE,
                mnuHelp.getIcon());
}//GEN-LAST:event_mnuHelpActionPerformed

    /**
     * Este método elimina un registro de la tabla FAEXPRESS. La base de datos
     * se encarga de vigilar la integridad referencial.
     *
     * @param pCodExpress (código de Express)
     * @throws java.sql.SQLException
     */
    public void eliminarRegistro(String pCodExpress) {
        if (pCodExpress == null) {
            return;
        }
        // end if

        if (JOptionPane.showConfirmDialog(null,
                "¿Realmente desea eliminar ese registro?")
                != JOptionPane.YES_OPTION) {
            return;
        }
        // end if

        String sqlDelete = "Delete from FAEXPRESS Where CodExpress = ?";
        PreparedStatement ps = null;
        int regAf = 0;
        try {
            ps = conn.prepareStatement(sqlDelete);
            ps.setString(1, pCodExpress);
            regAf = ps.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    cmdBorrar, ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (regAf > 0) {
            JOptionPane.showMessageDialog(cmdBorrar,
                    String.valueOf(regAf)
                    + " registros eliminados",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            txtCodExpress.setText(" ");
        } // end if

    } // end eliminar

    /**
     * @param c
     * @param codigoExp
     */
    public static void main(Connection c, javax.swing.JTextField codigoExp) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "TarifasExpress")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(TarifasExpress.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TarifasExpress run = new TarifasExpress(c, codigoExp);
        run.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAnterior;
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JButton cmdGuardar;
    private javax.swing.JButton cmdPrimero;
    private javax.swing.JButton cmdSiguiente;
    private javax.swing.JButton cmdUltimo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblFamilia;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenu mnuAyuda;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuHelp;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JSpinner spnPorcentaje;
    private javax.swing.JFormattedTextField txtCodExpress;
    private javax.swing.JFormattedTextField txtMinimo;
    private javax.swing.JFormattedTextField txtTarifa;
    // End of variables declaration//GEN-END:variables

    /**
     * Este método controla la acción para el botón guardar. Si el registro
     * existe lo modifica sino lo inserta. Hace una llamada al método
     * consultarRegistro para determinar si existe o no. Para insertar el
     * registro hace una llamada al procedimiento almacenado InsertarTarifa y le
     * pasa los parámetros requeridos.
     *
     * @throws java.sql.SQLException
     */
    private void guardarRegistro() {
        int sqlresult;
        String codExpress, minimo, tarifa, porcentaje, UpdateSql;

        codExpress = txtCodExpress.getText();

        try {
            minimo = Ut.quitarFormato(txtMinimo.getText());
            tarifa = Ut.quitarFormato(txtTarifa.getText());
        } catch (Exception ex) {
            Logger.getLogger(TarifasExpress.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        porcentaje = spnPorcentaje.getValue().toString();

        if (!consultarRegistro(codExpress)) {
            UpdateSql
                    = "CALL InsertarTarifa("
                    + codExpress + ","
                    + tarifa + ","
                    + minimo + ","
                    + porcentaje + ")";
        } else {
            UpdateSql
                    = "Update faexpress Set      "
                    + "minimo     = " + minimo + ","
                    + "tarifa     = " + tarifa + ","
                    + "porcentaje = " + porcentaje + " "
                    + "Where codExpress = " + codExpress;
        }

        try {
            sqlresult = sqlquery.executeUpdate(UpdateSql);
            if (sqlresult <= 0) {
                JOptionPane.showMessageDialog(null,
                        "El registro no se pudo guardar",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            rs = nav.cargarRegistro(
                    Navegador.ESPECIFICO,
                    codExpress,
                    tabla,
                    "codExpress");
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        JOptionPane.showMessageDialog(cmdGuardar,
                "Registro guardado satisfatoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    } // end guardar

    public void refrescarTarifa(int registro) {
        if (txtCodExpress.getText().trim().equals("")
                || txtCodExpress.getText().trim().equals("0")) {
            return;
        } // end if
        try {
            rs = nav.cargarRegistro(
                    registro,
                    txtCodExpress.getText(),
                    tabla, "CodExpress");
            rs.first();

            // Si el registro no existe
            // inicializo en cero los campos.
            if (rs.getRow() < 1) {
                txtMinimo.setText("0.00");
                txtTarifa.setText("0.00");
                spnPorcentaje.setValue(1.0);
            } else {
                txtCodExpress.setText(
                        rs.getString("CodExpress"));
                txtMinimo.setText(
                        Ut.setDecimalFormat(
                                rs.getString("Minimo"), "#,##0.00"));
                txtTarifa.setText(
                        Ut.setDecimalFormat(
                                rs.getString("Tarifa"), "#,##0.00"));
                spnPorcentaje.setValue(
                        rs.getFloat("Porcentaje"));
            }
            this.codigoExp.setText(txtCodExpress.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end refrescarTarifa

    public boolean consultarRegistro(String codExpress) {
        boolean existe = false;
        try {
            String sqlSent
                    = "Select Tarifa      "
                    + "from faexpress     "
                    + "Where CodExpress = " + codExpress;
            rs2 = sqlquery.executeQuery(sqlSent);
            existe = rs2.first();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        return existe;
    } // end consultarRegistro
} // end class
