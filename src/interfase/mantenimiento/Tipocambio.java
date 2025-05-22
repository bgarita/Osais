/*
 * Tipocambio.java
 *
 * Created on 31/07/2009, 07:36:28 PM
 */
package interfase.mantenimiento;

import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Navegador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
public class Tipocambio extends JFrame {

    private static final long serialVersionUID = 123L;
    private String codigoTC;

    public ResultSet rs;
    private final String tabla;
    private final Statement sqlquery;
    private Connection sConn = null;
    Navegador nav = null;
    private ResultSet rs2 = null;
    private ResultSet rs3 = null;   // Se usa para cargar las monedas
    private String codigo = null;   // Contiene el código de la moneda que esté en pantala
    private int nConsecutivo = 0;   // Se usa para la navegación (como llave)
    private boolean inicio = true;  // Se usa para evitar que corra el método del combo
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form
     *
     * @param c
     * @throws java.sql.SQLException
     */
    public Tipocambio(Connection c) throws SQLException {
        initComponents();
        tabla = "tipocambio";
        nav = new Navegador();

        sConn = c;

        sqlquery = sConn.createStatement();
        nav.setConexion(sConn);

        cargarCombo();
        inicio = false;

        String sqlSent
                = "Select * from tipocambio "
                + "Where nConsecutivo = (Select max(nConsecutivo) from tipocambio)";
        rs = nav.ejecutarQuery(sqlSent);

        if (rs == null || !rs.first()) {
            return;
        } // end if

        if (rs.getRow() < 1) {
            return;
        } // end if

        actualizarObjetos();

        // Cargar el código de tipo de cambio local.
        // El valor para este código debe ser siempre 1, caso constrario todos los
        // registros se multiplicarán por el valor que tenga.
        try (PreparedStatement ps = sConn.prepareStatement("Select codigoTC from config",
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rsTC = CMD.select(ps);
            if (UtilBD.goRecord(rsTC, UtilBD.FIRST)) {
                this.codigoTC = rsTC.getString("codigoTC");
            } // end if
        }

    } // end constructor

    private void actualizarObjetos() {
        try {
            codigo = rs.getString("codigo").trim();
            nConsecutivo = rs.getInt("nConsecutivo");
            // Ubico el código de moneda en el rs3
            if (rs3 == null) {
                return;
            } // end if
            rs3.beforeFirst();
            while (rs3.next()) {
                // Ubico el código en el ResultSet de monedas...
                if (rs3.getString("codigo").trim().equals(codigo)) {
                    // ... ahora ubico la descripción de moneda en el combo
                    for (int i = 0; i < cboDescrip.getItemCount(); i++) {
                        if (cboDescrip.getItemAt(i).equals(
                                rs3.getString("descrip").trim())) {
                            cboDescrip.setSelectedIndex(i);
                            break;
                        } // end if
                    } // end for
                } // end if
            } // end while

            txtTipoca.setText(rs.getString("tipoca"));
            DatFecha.setDate(rs.getDate("fecha"));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // actualizarObjetos

    @SuppressWarnings("unchecked")
    private void cargarCombo() {

        try {
            String sqlSent = "Select codigo,descrip from monedas";
            PreparedStatement ps = sConn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs3 = CMD.select(ps);
            if (rs3 == null) {
                return;
            } // end if
            Ut.fillComboBox(cboDescrip, rs3, 2, false);
        } catch (SQLException | EmptyDataSourceException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end cargarCombo

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFamilia = new javax.swing.JLabel();
        cmdPrimero = new javax.swing.JButton();
        cmdAnterior = new javax.swing.JButton();
        cmdSiguiente = new javax.swing.JButton();
        cmdUltimo = new javax.swing.JButton();
        cmdGuardar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        cboDescrip = new javax.swing.JComboBox<>();
        lblFamilia1 = new javax.swing.JLabel();
        lblFamilia2 = new javax.swing.JLabel();
        txtTipoca = new javax.swing.JFormattedTextField();
        lblImagen = new javax.swing.JLabel();
        DatFecha = new com.toedter.calendar.JDateChooser();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de tipos de cambio");

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Moneda");

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

        cboDescrip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDescripActionPerformed(evt);
            }
        });

        lblFamilia1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia1.setText("Fecha");

        lblFamilia2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia2.setText("Tipo de cambio");

        txtTipoca.setColumns(10);
        txtTipoca.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTipoca.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTipoca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTipocaFocusGained(evt);
            }
        });
        txtTipoca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTipocaActionPerformed(evt);
            }
        });

        lblImagen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/tipocambio.jpeg"))); // NOI18N

        DatFecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DatFechaPropertyChange(evt);
            }
        });

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

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 35, Short.MAX_VALUE)
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
                .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFamilia)
                            .addComponent(lblFamilia1)
                            .addComponent(lblFamilia2))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(lblImagen)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImagen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFamilia)
                    .addComponent(cboDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFamilia1))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFamilia2)
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(71, 71, 71)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdPrimero)
                    .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        try {
            this.sConn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Tipocambio.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed
        String fecha = Ut.fechaSQL(DatFecha.getDate());
        eliminarRegistro(codigo.trim(), fecha);
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void cmdPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrimeroActionPerformed
        try {
            rs = nav.cargarRegistro(Navegador.PRIMERO, nConsecutivo, tabla, "nConsecutivo");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            actualizarObjetos();
        } // try
        catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdPrimeroActionPerformed

    private void cmdAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnteriorActionPerformed
        try {
            rs = nav.cargarRegistro(Navegador.ANTERIOR, nConsecutivo, tabla, "nConsecutivo");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            actualizarObjetos();
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdAnteriorActionPerformed

    private void cmdSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSiguienteActionPerformed
        try {
            rs = nav.cargarRegistro(Navegador.SIGUIENTE, nConsecutivo, tabla, "nConsecutivo");
            if (rs == null) {
                return;
            } // end if
            rs.first();

            actualizarObjetos();
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdSiguienteActionPerformed

    private void cmdUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUltimoActionPerformed

        try {
            rs = nav.cargarRegistro(Navegador.ULTIMO, nConsecutivo, tabla, "nConsecutivo");
            if (rs == null) {
                return;
            } // end if
            rs.first();

            actualizarObjetos();
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdUltimoActionPerformed

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        guardarRegistro();
}//GEN-LAST:event_cmdGuardarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        mnuBorrarActionPerformed(null);
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void cboDescripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDescripActionPerformed
        if (inicio) {
            return;
        } // end if
        // Fecha actual con formato local
        Calendar cal = GregorianCalendar.getInstance();
        this.DatFecha.setDate(cal.getTime());
        ubicarCodigo();
        refrescartxtTipoca();
    }//GEN-LAST:event_cboDescripActionPerformed

    private void txtTipocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTipocaActionPerformed
        txtTipoca.transferFocus();
    }//GEN-LAST:event_txtTipocaActionPerformed

    private void txtTipocaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTipocaFocusGained
        txtTipoca.selectAll();
    }//GEN-LAST:event_txtTipocaFocusGained

    private void DatFechaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DatFechaPropertyChange
        if (this.inicio) {
            return;
        } // end if
        this.refrescartxtTipoca();
}//GEN-LAST:event_DatFechaPropertyChange

    /**
     * Este método hace una llamada a un SP para eliminar un registro y deposita en la
     * variable sqlResult la cantidad de registros eliminados. El parámetro pFecha viene
     * con formato SQL incluyendo las comillas
     *
     * @param pCodigo, pFecha
     * @throws java.sql.SQLException
     */
    private void eliminarRegistro(String pCodigo, String pFecha) {
        if (pCodigo == null || pFecha == null) {
            return;
        } // end if

        if (JOptionPane.showConfirmDialog(null,
                "¿Está seguro de querer eliminar ese registro?")
                != JOptionPane.YES_OPTION) {
            return;
        } // end if

        String sqlDelete = "CALL EliminarTipoCa('"
                + pCodigo + "'" + "," + pFecha + ")";
        int sqlResult = 0;
        try {
            sqlResult = sqlquery.executeUpdate(sqlDelete);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        if (sqlResult > 0) {
            JOptionPane.showMessageDialog(cmdBorrar,
                    sqlResult + " registros eliminados",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            codigo = "";
            DatFecha.setDate(null);
            txtTipoca.setText("0.00");
        } // end if

    } // end eliminar

    /**
     * @param c
     */
    public static void main(Connection c) {
        try {
            //JFrame.setDefaultLookAndFeelDecorated(true);
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "Tipocambio")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Tipocambio.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Tipocambio run = new Tipocambio(c);
            run.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatFecha;
    private javax.swing.JComboBox<String> cboDescrip;
    private javax.swing.JButton cmdAnterior;
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JButton cmdGuardar;
    private javax.swing.JButton cmdPrimero;
    private javax.swing.JButton cmdSiguiente;
    private javax.swing.JButton cmdUltimo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblFamilia;
    private javax.swing.JLabel lblFamilia1;
    private javax.swing.JLabel lblFamilia2;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtTipoca;
    // End of variables declaration//GEN-END:variables

    private void guardarRegistro() {
        String tipoca = "0";
        this.setAlwaysOnTop(false);
        try {
            tipoca = Ut.quitarFormato(txtTipoca).toString();
        } catch (ParseException ex) {
            Logger.getLogger(Tipocambio.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (Float.parseFloat(tipoca) < 1.0) {
            JOptionPane.showMessageDialog(null,
                    "Tipo de cambio erróneo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtTipoca.requestFocusInWindow();
            return;
        } // end if

        String fecha = Ut.fechaSQL(DatFecha.getDate());

        // Determino cuál es el código en Rs3
        if (rs3 == null) {
            JOptionPane.showMessageDialog(null,
                    "No hay monedas definidas."
                    + "\nDebe ir al menú mantenimiento y crear las monedas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        ubicarCodigo();

        // Si el registro a guardar corresponde a la moneda local, no se debe permitir
        // guardar un valor distinto de 1 (uno).
        if (this.codigo.equals(this.codigoTC) && Float.parseFloat(tipoca) != 1f) {
            tipoca = "1.00";
            this.txtTipoca.setText(tipoca);
            JOptionPane.showMessageDialog(cmdGuardar,
                    "El tipo de cambio para la moneda local no debe ser distinto de 1.00\n"+
                    "Se corrigió automáticamente!"        ,
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        } // end if
        
        int sqlresult = 0;
        String updateSql
                = "CALL InsertarTipoca('"
                + codigo + "',"
                + fecha + ", "
                + tipoca + ")";

        // Si el registro ya existe, se hace un update.
        if (consultarRegistro(codigo, fecha)) {
            updateSql
                    = "Update tipocambio Set tipoca = " + tipoca
                    + " where codigo = " + "'" + codigo + "'" + " and "
                    + "fecha = " + fecha;
        }

        try {
            sqlresult = sqlquery.executeUpdate(updateSql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        if (sqlresult <= 0) {
            JOptionPane.showMessageDialog(cmdGuardar,
                    "El registro no se pudo guardar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        JOptionPane.showMessageDialog(cmdGuardar,
                "Registro guardado satisfatoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    } // end guardar

    public void refrescartxtTipoca() {
        if (codigo == null || codigo.trim().equals("")) {
            return;
        } // end if
        String fecha = Ut.fechaSQL(DatFecha.getDate());

        if (consultarRegistro(codigo, fecha)) {
            try {
                // Esta consulta deja en rs2 el tipo de cambio
                txtTipoca.setText(String.valueOf(rs2.getFloat(1)).trim());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }
        } else {
            txtTipoca.setText("0.00");
        } // end if

    } // end refrescartxtTipoca

    /**
     * Este método verifica si un tipo de cambio existe o no para una fecha específica.
     * Para lograrlo realiza una llamada a una función almacenada cuyo valor devuelto es
     * el tipo de cambio (si existe) para esa fecha o null (si no existe).
     *
     * @param pCodigo String código de moneda a consultar.
     * @param pFecha String (debe venir con formato SQL incluyendo las comillas)
     * @return (true = existe, false = no existe)
     */
    public boolean consultarRegistro(String pCodigo, String pFecha) {
        boolean existe = false;
        try {
            String sqlSent
                    = "SELECT ConsultarTipoca("
                    + "'" + pCodigo + "'" + "," + pFecha + ")";
            rs2 = sqlquery.executeQuery(sqlSent);
            if (rs2 != null) {
                rs2.first();
                if (rs2.getRow() == 1 && rs2.getString(1) != null) {
                    existe = true;
                } // end if
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        return existe;
    } // end consultarRegistro

    private void ubicarCodigo() {
        try {
            // Busco el código que corresponde a la moneda del combo
            if (rs3 == null) {
                return;
            } // end if

            rs3.beforeFirst();
            while (rs3.next()) {
                if (cboDescrip.getSelectedItem().toString().trim().equals(rs3.getString("descrip").trim())) {
                    codigo = rs3.getString("codigo").trim();
                    break;
                } // end if
            } // end while
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end ubicarCodigo
} // end class
